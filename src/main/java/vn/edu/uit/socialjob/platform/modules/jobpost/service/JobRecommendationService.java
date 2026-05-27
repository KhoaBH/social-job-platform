package vn.edu.uit.socialjob.platform.modules.jobpost.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.education.entity.Education;
import vn.edu.uit.socialjob.platform.modules.education.repository.EducationRepository;
import vn.edu.uit.socialjob.platform.modules.experience.entity.WorkExperience;
import vn.edu.uit.socialjob.platform.modules.experience.repository.WorkExperienceRepository;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobPostRepository;
import vn.edu.uit.socialjob.platform.modules.apply.repository.ApplyRepository;
import vn.edu.uit.socialjob.platform.modules.skill.entity.UserSkill;
import vn.edu.uit.socialjob.platform.modules.skill.repository.UserSkillRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;
import vn.edu.uit.socialjob.platform.config.JobEmbeddingProperties;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JobRecommendationService {

    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final UserSkillRepository userSkillRepository;
    private final JobPostRepository jobPostRepository;
    private final ApplyRepository applyRepository;
    private final JobEmbeddingProperties embeddingProperties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public JobRecommendationService(
        UserRepository userRepository,
        EducationRepository educationRepository,
        WorkExperienceRepository workExperienceRepository,
        UserSkillRepository userSkillRepository,
        JobPostRepository jobPostRepository,
        ApplyRepository applyRepository,
        JobEmbeddingProperties embeddingProperties
    ) {
        this.userRepository = userRepository;
        this.educationRepository = educationRepository;
        this.workExperienceRepository = workExperienceRepository;
        this.userSkillRepository = userSkillRepository;
        this.jobPostRepository = jobPostRepository;
        this.applyRepository = applyRepository;
        this.embeddingProperties = embeddingProperties;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofMillis(embeddingProperties.getTimeoutMillis()))
            .version(HttpClient.Version.HTTP_1_1)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<JobPost> getRecommendedJobs(
        UUID userId,
        String text,
        String location,
        Integer minSalary,
        Integer maxSalary,
        String dateCreateGte,
        Integer applyCountGte,
        Integer topK
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Build query text from CV + optional FE text
        String cvText = buildCVText(userId);
        String queryText = mergeQueryText(cvText, text);
        System.out.println("[RECOMMENDATION] Query text for user " + userId + ":\n" + queryText);

        // Call bert_service /api/v1/recommend endpoint
        List<String> cvSkills = userSkillRepository.findByUserId(userId).stream()
            .map(us -> us.getSkill() != null ? us.getSkill().getId().toString() : null)
            .filter(name -> name != null && !name.isBlank())
            .toList();
        System.out.println("[RECOMMENDATION] CV skills for user " + userId + ": " + cvSkills);
        Map<String, Object> filters = new LinkedHashMap<>();
        if (location != null && !location.isBlank()) {
            filters.put("location", location.trim());
        }
        if (minSalary != null) {
            filters.put("min_salary_gte", minSalary);
        }
        if (maxSalary != null) {
            filters.put("max_salary_lte", maxSalary);
        }
        if (dateCreateGte != null && !dateCreateGte.isBlank()) {
            filters.put("date_create_gte", dateCreateGte.trim());
        }
        if (applyCountGte != null) {
            filters.put("apply_count_gte", applyCountGte);
        }

        int resolvedTopK = topK == null || topK <= 0 ? 10 : topK;
        List<String> recommendedJobIds = callBertRecommendationApi(cvText,text, cvSkills, filters, resolvedTopK);
        System.out.println("[RECOMMENDATION] Recommended job IDs: " + recommendedJobIds);

        java.util.Set<UUID> appliedJobIds = applyRepository.findAllActiveByUserId(userId).stream()
            .map(apply -> apply.getJobPost() != null ? apply.getJobPost().getId() : null)
            .filter(java.util.Objects::nonNull)
            .collect(java.util.stream.Collectors.toSet());
        System.out.println("[RECOMMENDATION] Applied job IDs for user " + userId + ": " + appliedJobIds);

        // Fetch job posts from database
        return recommendedJobIds.stream()
            .map(UUID::fromString)
            .map(id -> {
                JobPost job = jobPostRepository.findById(id).orElse(null);
                if (job != null) {
                    job.setApplyStatus(appliedJobIds.contains(id));
                }
                return job;
            })
            .filter(job -> job != null && !job.isDeleted())
            .toList();
    }

    private String mergeQueryText(String cvText, String textFromFe) {
        String base = cvText == null ? "" : cvText.trim();
        if (textFromFe == null || textFromFe.isBlank()) {
            return base;
        }

        if (base.isBlank()) {
            return textFromFe.trim();
        }

        return base + "\nSearch: " + textFromFe.trim();
    }

    private String buildCVText(UUID userId) {
        StringBuilder cvBuilder = new StringBuilder();

        // User basic info
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getFullName() != null) {
            cvBuilder.append("Name: ").append(user.getFullName()).append("\n");
        }
        if (user.getHeadline() != null) {
            cvBuilder.append("Headline: ").append(user.getHeadline()).append("\n");
        }
        if (user.getSummary() != null) {
            cvBuilder.append("Summary: ").append(user.getSummary()).append("\n");
        }
        if (user.getLocation() != null) {
            cvBuilder.append("Location: ").append(user.getLocation()).append("\n");
        }
        if(user.getProfileText() != null) {
            cvBuilder.append("Profile: ").append(user.getProfileText()).append("\n");
        }

        // Education
        List<Education> educations = educationRepository.findByUserId(userId);
        if (!educations.isEmpty()) {
            cvBuilder.append("\nEducation:\n");
            for (Education edu : educations) {
                if (edu.getSchool() != null && edu.getSchool().getName() != null) {
                    cvBuilder.append("- ").append(edu.getSchool().getName());
                } else if (edu.getSchoolName() != null) {
                    cvBuilder.append("- ").append(edu.getSchoolName());
                }
                if (edu.getDegree() != null) {
                    cvBuilder.append(" (").append(edu.getDegree()).append(")");
                }
                if (edu.getFieldOfStudy() != null && edu.getFieldOfStudy().getName() != null) {
                    cvBuilder.append(" - ").append(edu.getFieldOfStudy().getName());
                }
                cvBuilder.append("\n");
            }
        }

        // Work Experience
        List<WorkExperience> experiences = workExperienceRepository.findByUserId(userId);
        if (!experiences.isEmpty()) {
            cvBuilder.append("\nWork Experience:\n");
            for (WorkExperience exp : experiences) {
                cvBuilder.append("- ").append(exp.getJobTitle());
                if (exp.getCompany() != null && exp.getCompany().getName() != null) {
                    cvBuilder.append(" at ").append(exp.getCompany().getName());
                } else if (exp.getCompanyName() != null) {
                    cvBuilder.append(" at ").append(exp.getCompanyName());
                }
                if (exp.getDescription() != null) {
                    cvBuilder.append(": ").append(exp.getDescription());
                }
                cvBuilder.append("\n");
            }
        }

        // Skills
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        if (!userSkills.isEmpty()) {
            cvBuilder.append("\nSkills: ");
            String skillsStr = userSkills.stream()
                .map(us -> us.getSkill() != null ? us.getSkill().getName() : "Unknown")
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
            cvBuilder.append(skillsStr).append("\n");
        }

        return cvBuilder.toString().trim();
    }

    @SuppressWarnings("unchecked")
    private List<String> callBertRecommendationApi(String cvText,String text, List<String> cvSkills, Map<String, Object> filters, int topK) {
        try {
            String url = embeddingProperties.getBaseUrl() + "/api/v1/recommend";
            Map<String, Object> payload = new java.util.LinkedHashMap<>();
            payload.put("text", cvText);
            payload.put("top_k", topK);
            payload.put("keyword", text);
            if (cvSkills != null && !cvSkills.isEmpty()) {
                payload.put("skill_ids", cvSkills);
            }
            if (filters != null && !filters.isEmpty()) {
                payload.put("filters", filters);
            }

            String jsonPayload = objectMapper.writeValueAsString(payload);
            System.out.println("[RECOMMENDATION] Payload to bert_service: " + jsonPayload);
            System.out.println("[RECOMMENDATION] POST URL: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofMillis(embeddingProperties.getTimeoutMillis()))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("[RECOMMENDATION] Error from bert_service: " + response.body());
                return List.of();
            }

            // Parse response - expecting: {"recommendations": [{"original_id":"id","relevance_score":...,"mmr_score":...}, ...]}
            Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> recObjects = (List<Map<String, Object>>) responseBody.get("recommendations");

            if (recObjects == null || recObjects.isEmpty()) {
                return List.of();
            }

            // Log received ids and mmr scores for visibility
            List<String> ids = recObjects.stream()
                .map(m -> (String) m.get("original_id"))
                .toList();

            List<String> scoreLog = recObjects.stream()
                .map(m -> {
                    Object id = m.get("original_id");
                    Object mmr = m.get("mmr_score");
                    return id + ":" + mmr;
                }).toList();

            System.out.println("[RECOMMENDATION] Received from bert_service: " + scoreLog);

            return ids;
        } catch (Exception e) {
            System.err.println("[RECOMMENDATION] Error calling bert_service: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
