package vn.edu.uit.socialjob.platform.modules.post.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;
import vn.edu.uit.socialjob.platform.common.enums.VisibilityStatus;

@Value
@Builder
public class PostFeedRecommendationResponse {
    UUID postId;
    UUID authorId;
    String authorUsername;
    String authorFullName;
    String authorAvatarUrl;
    UUID companyId;
    String content;
    String imageUrl;
    VisibilityStatus visibility;
    LocalDateTime postedAt;
    int circleLevel;
    long likeCount;
    long commentCount;
    long recentLikeCount;
    long recentCommentCount;
    long likeGrowth;
    long commentGrowth;
    double score;
}
