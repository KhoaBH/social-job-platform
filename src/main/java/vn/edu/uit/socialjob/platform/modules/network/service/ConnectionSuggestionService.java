package vn.edu.uit.socialjob.platform.modules.network.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.education.service.EducationService;
import vn.edu.uit.socialjob.platform.modules.experience.service.WorkExperienceService;
import vn.edu.uit.socialjob.platform.modules.network.dto.SuggestionDTO;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ConnectionSuggestionService {

    private final ConnectionService connectionService;
    private final WorkExperienceService workExperienceService;
    private final EducationService educationService;
    public List<SuggestionDTO> suggest(UUID userId) {

        // ======================
        // 1. PRELOAD DATA
        // ======================
        Set<UUID> myFriends = new HashSet<>(
                connectionService.getFriendIds(userId)
        );

        Set<UUID> myCompanies = new HashSet<>(
                workExperienceService.getCompanyIdsByUser(userId)
        );

        Set<UUID> mySchools = new HashSet<>(
                educationService.getSchoolIdsByUser(userId)
        );

        // ======================
        // 2. CANDIDATES
        // ======================
        Set<UUID> candidates = new HashSet<>();

        // friend-of-friend
        for (UUID f : myFriends) {
            candidates.addAll(connectionService.getFriendIds(f));
        }

        // company
        for (UUID companyId : myCompanies) {
            candidates.addAll(workExperienceService.getUsersByCompany(companyId));
        }

        // school
        for (UUID schoolId : mySchools) {
            candidates.addAll(educationService.getUsersBySchool(schoolId));
        }

        // popular
        candidates.addAll(connectionService.getPopularUsers());

        // remove invalid
        candidates.remove(userId);
        candidates.removeAll(myFriends);

        // ======================
        // 3. PRELOAD FRIEND MAP
        // ======================
        Map<UUID, Set<UUID>> friendMap = new HashMap<>();

        for (UUID c : candidates) {
            friendMap.put(c, connectionService.getFriendIds(c));
        }

        // ======================
        // 4. SCORING
        // ======================
        List<SuggestionDTO> result = new ArrayList<>();

        for (UUID c : candidates) {

            Set<UUID> theirFriends = friendMap.getOrDefault(c, Set.of());

            // ===== MUTUAL =====
            Set<UUID> mutual = new HashSet<>(myFriends);
            mutual.retainAll(theirFriends);
            int mutualCount = mutual.size();

            // ===== JACCARD =====
            Set<UUID> union = new HashSet<>(myFriends);
            union.addAll(theirFriends);

            double jaccard = union.isEmpty() ? 0 :
                    (double) mutualCount / union.size();

            // ===== ADAMIC =====
            double adamic = 0.0;
            for (UUID z : mutual) {
                int degree = connectionService.getFriendIds(z).size();
                if (degree > 1) {
                    adamic += 1.0 / Math.log(degree);
                }
            }

            // ===== COMPANY =====
            Set<UUID> candidateCompanies =
                    workExperienceService.getCompanyIdsByUser(c);

            Set<UUID> companyOverlap = new HashSet<>(myCompanies);
            companyOverlap.retainAll(candidateCompanies);

            int companyScore = companyOverlap.isEmpty() ? 0 : 5;

            // ===== SCHOOL =====
            Set<UUID> candidateSchools =
                    educationService.getSchoolIdsByUser(c);

            Set<UUID> schoolOverlap = new HashSet<>(mySchools);
            schoolOverlap.retainAll(candidateSchools);

            int schoolScore = schoolOverlap.isEmpty() ? 0 : 3;

            // ===== POPULAR =====
            double popularityScore = theirFriends.size() * 0.1;

            // ===== FINAL SCORE =====
            double score =
                    mutualCount * 3 +
                            jaccard * 5 +
                            adamic * 8 +
                            companyScore +
                            schoolScore +
                            popularityScore;

            if (score > 0) {
                result.add(new SuggestionDTO(c, score, mutualCount));
            }
        }

        // ======================
        // 5. SORT
        // ======================
        result.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return result.stream().limit(10).toList();
    }
}