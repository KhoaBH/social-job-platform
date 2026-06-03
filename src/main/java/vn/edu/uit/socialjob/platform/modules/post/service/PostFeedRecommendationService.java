package vn.edu.uit.socialjob.platform.modules.post.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.edu.uit.socialjob.platform.common.enums.ConnectionStatus;
import vn.edu.uit.socialjob.platform.modules.network.entity.Connection;
import vn.edu.uit.socialjob.platform.modules.network.entity.Follow;
import vn.edu.uit.socialjob.platform.modules.network.repository.ConnectionRepository;
import vn.edu.uit.socialjob.platform.modules.network.repository.FollowRepository;
import vn.edu.uit.socialjob.platform.modules.post.dto.PostFeedRecommendationResponse;
import vn.edu.uit.socialjob.platform.modules.post.entity.Post;
import vn.edu.uit.socialjob.platform.modules.post.repository.PostCommentRepository;
import vn.edu.uit.socialjob.platform.modules.post.repository.PostInteractionRepository;
import vn.edu.uit.socialjob.platform.modules.post.repository.PostMetricCountProjection;
import vn.edu.uit.socialjob.platform.modules.post.repository.PostRepository;

@Service
public class PostFeedRecommendationService {

    private static final int DEFAULT_WINDOW_HOURS = 24;
    private static final int DEFAULT_LOOKBACK_DAYS = 14;
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;

    private final PostRepository postRepository;
    private final PostInteractionRepository postInteractionRepository;
    private final PostCommentRepository postCommentRepository;
    private final FollowRepository followRepository;
    private final ConnectionRepository connectionRepository;

    public PostFeedRecommendationService(
        PostRepository postRepository,
        PostInteractionRepository postInteractionRepository,
        PostCommentRepository postCommentRepository,
        FollowRepository followRepository,
        ConnectionRepository connectionRepository
    ) {
        this.postRepository = postRepository;
        this.postInteractionRepository = postInteractionRepository;
        this.postCommentRepository = postCommentRepository;
        this.followRepository = followRepository;
        this.connectionRepository = connectionRepository;
    }

    public List<PostFeedRecommendationResponse> recommend(
        UUID viewerId,
        Integer windowHours,
        Integer lookbackDays,
        Integer limit
    ) {
        int resolvedWindowHours = resolveWindowHours(windowHours);
        int resolvedLookbackDays = resolveLookbackDays(lookbackDays);
        int resolvedLimit = resolveLimit(limit);

        Map<UUID, Integer> authorCircleLevels = buildAuthorCircleLevels(viewerId);
        if (authorCircleLevels.isEmpty()) {
            return List.of();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAfter = now.minusDays(resolvedLookbackDays);
        List<Post> candidates = postRepository.findRecentByAuthorIds(authorCircleLevels.keySet(), createdAfter);
        if (candidates.isEmpty()) {
            return List.of();
        }

        List<UUID> postIds = candidates.stream().map(Post::getId).toList();
        LocalDateTime currentWindowStart = now.minusHours(resolvedWindowHours);
        LocalDateTime previousWindowStart = currentWindowStart.minusHours(resolvedWindowHours);

        Map<UUID, Long> totalLikes = toCountMap(postInteractionRepository.countActiveByPostIds(postIds));
        Map<UUID, Long> totalComments = toCountMap(postCommentRepository.countActiveByPostIds(postIds));
        Map<UUID, Long> currentLikes = toCountMap(postInteractionRepository.countActiveByPostIdsBetween(postIds, currentWindowStart, now));
        Map<UUID, Long> previousLikes = toCountMap(postInteractionRepository.countActiveByPostIdsBetween(postIds, previousWindowStart, currentWindowStart));
        Map<UUID, Long> currentComments = toCountMap(postCommentRepository.countActiveByPostIdsBetween(postIds, currentWindowStart, now));
        Map<UUID, Long> previousComments = toCountMap(postCommentRepository.countActiveByPostIdsBetween(postIds, previousWindowStart, currentWindowStart));

        return candidates.stream()
            .map(post -> toRecommendation(
                post,
                authorCircleLevels.getOrDefault(post.getAuthor().getId(), 2),
                totalLikes,
                totalComments,
                currentLikes,
                previousLikes,
                currentComments,
                previousComments,
                now
            ))
            .sorted(Comparator.comparingDouble(PostFeedRecommendationResponse::getScore).reversed()
                .thenComparing(PostFeedRecommendationResponse::getPostedAt, Comparator.reverseOrder()))
            .limit(resolvedLimit)
            .toList();
    }

    private Map<UUID, Integer> buildAuthorCircleLevels(UUID viewerId) {
        Set<UUID> directCircle = new LinkedHashSet<>();
        directCircle.addAll(
            followRepository.findByFollowerId(viewerId).stream()
                .map(follow -> follow.getFollowee().getId())
                .collect(Collectors.toSet())
        );
        directCircle.addAll(getAcceptedConnectionIds(viewerId));
        directCircle.remove(viewerId);

        Set<UUID> secondCircle = new LinkedHashSet<>();
        for (UUID directUserId : directCircle) {
            secondCircle.addAll(
                followRepository.findByFollowerId(directUserId).stream()
                    .map(Follow::getFollowee)
                    .map(followee -> followee.getId())
                    .toList()
            );
            secondCircle.addAll(getAcceptedConnectionIds(directUserId));
        }

        secondCircle.remove(viewerId);
        secondCircle.removeAll(directCircle);

        Map<UUID, Integer> result = new LinkedHashMap<>();
        directCircle.forEach(authorId -> result.put(authorId, 1));
        secondCircle.forEach(authorId -> result.putIfAbsent(authorId, 2));
        return result;
    }

    private Set<UUID> getAcceptedConnectionIds(UUID userId) {
        return connectionRepository.findAllByUserId(userId).stream()
            .filter(connection -> connection.getStatus() == ConnectionStatus.ACCEPTED)
            .map(connection -> resolveCounterpartyId(connection, userId))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private UUID resolveCounterpartyId(Connection connection, UUID userId) {
        if (connection.getRequester().getId().equals(userId)) {
            return connection.getAddressee().getId();
        }
        return connection.getRequester().getId();
    }

    private Map<UUID, Long> toCountMap(Collection<PostMetricCountProjection> rows) {
        Map<UUID, Long> counts = new HashMap<>();
        for (PostMetricCountProjection row : rows) {
            counts.put(row.getPostId(), row.getCount());
        }
        return counts;
    }

    private PostFeedRecommendationResponse toRecommendation(
        Post post,
        int circleLevel,
        Map<UUID, Long> totalLikes,
        Map<UUID, Long> totalComments,
        Map<UUID, Long> currentLikes,
        Map<UUID, Long> previousLikes,
        Map<UUID, Long> currentComments,
        Map<UUID, Long> previousComments,
        LocalDateTime now
    ) {
        UUID postId = post.getId();
        long likeCount = totalLikes.getOrDefault(postId, 0L);
        long commentCount = totalComments.getOrDefault(postId, 0L);
        long recentLikeCount = currentLikes.getOrDefault(postId, 0L);
        long recentCommentCount = currentComments.getOrDefault(postId, 0L);
        long previousLikeCount = previousLikes.getOrDefault(postId, 0L);
        long previousCommentCount = previousComments.getOrDefault(postId, 0L);
        long likeGrowth = Math.max(0L, recentLikeCount - previousLikeCount);
        long commentGrowth = Math.max(0L, recentCommentCount - previousCommentCount);

        double score = calculateScore(
            circleLevel,
            likeCount,
            commentCount,
            recentLikeCount,
            recentCommentCount,
            likeGrowth,
            commentGrowth,
            post.getCreatedAt(),
            now
        );

        return PostFeedRecommendationResponse.builder()
            .postId(postId)
            .authorId(post.getAuthor().getId())
            .authorUsername(post.getAuthor().getUsername())
            .authorFullName(post.getAuthor().getFullName())
            .authorAvatarUrl(post.getAuthor().getAvatarUrl())
            .companyId(post.getCompanyId())
            .content(post.getContent())
            .imageUrl(post.getImageUrl())
            .visibility(post.getVisibility())
            .postedAt(post.getCreatedAt())
            .circleLevel(circleLevel)
            .likeCount(likeCount)
            .commentCount(commentCount)
            .recentLikeCount(recentLikeCount)
            .recentCommentCount(recentCommentCount)
            .likeGrowth(likeGrowth)
            .commentGrowth(commentGrowth)
            .score(score)
            .build();
    }

    private double calculateScore(
        int circleLevel,
        long likeCount,
        long commentCount,
        long recentLikeCount,
        long recentCommentCount,
        long likeGrowth,
        long commentGrowth,
        LocalDateTime postedAt,
        LocalDateTime now
    ) {
        double circleScore = circleLevel == 1 ? 40.0 : 20.0;
        double engagementScore = Math.log1p(likeCount) * 6.0 + Math.log1p(commentCount) * 8.0;
        double velocityScore = Math.log1p(recentLikeCount) * 10.0 + Math.log1p(recentCommentCount) * 12.0;
        double growthScore = likeGrowth * 3.0 + commentGrowth * 4.0;

        long ageHours = Math.max(0L, Duration.between(postedAt, now).toHours());
        double freshnessScore = 30.0 / (1.0 + (ageHours / 12.0));

        return circleScore + engagementScore + velocityScore + growthScore + freshnessScore;
    }

    private int resolveWindowHours(Integer windowHours) {
        if (windowHours == null || windowHours <= 0) {
            return DEFAULT_WINDOW_HOURS;
        }
        return Math.min(windowHours, 24 * 14);
    }

    private int resolveLookbackDays(Integer lookbackDays) {
        if (lookbackDays == null || lookbackDays <= 0) {
            return DEFAULT_LOOKBACK_DAYS;
        }
        return Math.min(lookbackDays, 90);
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
