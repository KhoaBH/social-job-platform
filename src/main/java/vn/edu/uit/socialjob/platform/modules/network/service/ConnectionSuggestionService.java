package vn.edu.uit.socialjob.platform.modules.network.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.education.service.EducationService;
import vn.edu.uit.socialjob.platform.modules.experience.service.WorkExperienceService;
import vn.edu.uit.socialjob.platform.modules.network.dto.SuggestionDTO;
import vn.edu.uit.socialjob.platform.modules.network.dto.SuggestedUserDTO;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionSuggestionService {

    private final ConnectionService connectionService;
    private final WorkExperienceService workExperienceService;
    private final EducationService educationService;
    private final UserService userService;

    public List<SuggestionDTO> suggest(UUID userId) {
        // ======================
        // 1. PRELOAD DATA
        // ======================
        Set<UUID> myFriends = new HashSet<>(connectionService.getFriendIds(userId));
        Set<UUID> myCompanies = new HashSet<>(workExperienceService.getCompanyIdsByUser(userId));
        Set<UUID> mySchools = new HashSet<>(educationService.getSchoolIdsByUser(userId));

        // ======================
        // 2. FIND CANDIDATES
        // ======================
        Set<UUID> candidates = findPotentialCandidates(userId, myFriends, myCompanies, mySchools);

        // ======================
        // 3. BATCH PRELOAD: Friends + Companies + Schools
        // ======================
        Map<UUID, Set<UUID>> friendMap = buildFriendMap(candidates);
        Map<UUID, Set<UUID>> companiesMap = buildCompaniesMap(candidates);
        Map<UUID, Set<UUID>> schoolsMap = buildSchoolsMap(candidates);

        // Cache friend degrees (để tránh gọi getFriendIds() lại trong Adamic Adar)
        Map<UUID, Integer> friendDegrees = buildFriendDegrees(friendMap.values()
                .stream().flatMap(Set::stream).collect(Collectors.toSet()));

        // ======================
        // 4. SCORING WITH PARALLEL STREAM
        // ======================
        return candidates.parallelStream()
                .map(candidateId -> calculateScore(
                        candidateId, myFriends, myCompanies, mySchools,
                        friendMap, companiesMap, schoolsMap, friendDegrees
                ))
                .filter(dto -> dto.getScore() > 0)
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(10)
                .toList();
    }

    /**
     * Trả về danh sách User đã được sắp xếp theo gợi ý (kèm score và mutual count)
     */
    public List<SuggestedUserDTO> suggestUsers(UUID userId) {
        return suggest(userId).stream()
                .map(dto -> new SuggestedUserDTO(
                    userService.getById(dto.getUserId()),
                    dto.getScore(),
                    dto.getMutual()
                ))
                .toList();
    }

    /**
     * Tìm các ứng viên gợi ý dựa trên:
     * - Bạn của bạn (friend-of-friend)
     * - Đồng nghiệp
     * - Cùng trường
     * - Top popular users
     */
    private Set<UUID> findPotentialCandidates(UUID userId, Set<UUID> myFriends,
                                              Set<UUID> myCompanies, Set<UUID> mySchools) {
        Set<UUID> candidates = new HashSet<>();

        // friend-of-friend
        for (UUID friend : myFriends) {
            candidates.addAll(connectionService.getFriendIds(friend));
        }

        // company
        for (UUID companyId : myCompanies) {
            candidates.addAll(workExperienceService.getUsersByCompany(companyId));
        }

        // school
        for (UUID schoolId : mySchools) {
            candidates.addAll(educationService.getUsersBySchool(schoolId));
        }

        // popular users
        candidates.addAll(connectionService.getPopularUsers());

        // remove invalid
        candidates.remove(userId);
        candidates.removeAll(myFriends);

        return candidates;
    }

    /**
     * Lấy danh sách bạn của mỗi candidate (batch load)
     */
    private Map<UUID, Set<UUID>> buildFriendMap(Set<UUID> candidates) {
        Map<UUID, Set<UUID>> friendMap = new HashMap<>();
        for (UUID c : candidates) {
            friendMap.put(c, connectionService.getFriendIds(c));
        }
        return friendMap;
    }

    /**
     * Lấy danh sách công ty của mỗi candidate (batch load)
     */
    private Map<UUID, Set<UUID>> buildCompaniesMap(Set<UUID> candidates) {
        Map<UUID, Set<UUID>> companiesMap = new HashMap<>();
        for (UUID c : candidates) {
            companiesMap.put(c, workExperienceService.getCompanyIdsByUser(c));
        }
        return companiesMap;
    }

    /**
     * Lấy danh sách trường của mỗi candidate (batch load)
     */
    private Map<UUID, Set<UUID>> buildSchoolsMap(Set<UUID> candidates) {
        Map<UUID, Set<UUID>> schoolsMap = new HashMap<>();
        for (UUID c : candidates) {
            schoolsMap.put(c, educationService.getSchoolIdsByUser(c));
        }
        return schoolsMap;
    }

    /**
     * Cache friend degrees (số lượng bạn) để tránh gọi getFriendIds() lại nhiều lần
     */
    private Map<UUID, Integer> buildFriendDegrees(Set<UUID> allFriends) {
        Map<UUID, Integer> degrees = new HashMap<>();
        for (UUID friend : allFriends) {
            degrees.put(friend, connectionService.getFriendIds(friend).size());
        }
        return degrees;
    }

    /**
     * Tính điểm gợi ý cho một candidate duy nhất
     */
    private SuggestionDTO calculateScore(UUID candidateId,
                                        Set<UUID> myFriends, Set<UUID> myCompanies, Set<UUID> mySchools,
                                        Map<UUID, Set<UUID>> friendMap,
                                        Map<UUID, Set<UUID>> companiesMap,
                                        Map<UUID, Set<UUID>> schoolsMap,
                                        Map<UUID, Integer> friendDegrees) {

        Set<UUID> theirFriends = friendMap.getOrDefault(candidateId, Set.of());

        // ===== MUTUAL =====
        Set<UUID> mutual = new HashSet<>(myFriends);
        mutual.retainAll(theirFriends);
        int mutualCount = mutual.size();

        // ===== JACCARD =====
        Set<UUID> union = new HashSet<>(myFriends);
        union.addAll(theirFriends);
        double jaccard = union.isEmpty() ? 0 : (double) mutualCount / union.size();

        // ===== ADAMIC ADAR (sử dụng cache friendDegrees) =====
        double adamic = mutual.stream()
                .mapToDouble(z -> {
                    Integer degree = friendDegrees.get(z);
                    return (degree != null && degree > 1) ? 1.0 / Math.log(degree) : 0.0;
                })
                .sum();

        // ===== COMPANY =====
        Set<UUID> theirCompanies = companiesMap.getOrDefault(candidateId, Set.of());
        long companyOverlapCount = theirCompanies.stream().filter(myCompanies::contains).count();
        int companyScore = companyOverlapCount > 0 ? 5 : 0;

        // ===== SCHOOL =====
        Set<UUID> theirSchools = schoolsMap.getOrDefault(candidateId, Set.of());
        long schoolOverlapCount = theirSchools.stream().filter(mySchools::contains).count();
        int schoolScore = schoolOverlapCount > 0 ? 3 : 0;

        // ===== POPULARITY =====
        double popularityScore = theirFriends.size() * 0.1;

        // ===== FINAL SCORE =====
        double score = (mutualCount * 3.0) +
                       (jaccard * 5.0) +
                       (adamic * 8.0) +
                       companyScore +
                       schoolScore +
                       popularityScore;

        return new SuggestionDTO(candidateId, score, mutualCount);
    }
}