package vn.edu.uit.socialjob.platform.modules.jobpost.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.config.JobEmbeddingProperties;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.SearchJobRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobPostRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobSearchService {

    private final JobPostRepository jobPostRepository;
    private final JobEmbeddingProperties embeddingProperties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public JobSearchService(
            JobPostRepository jobPostRepository,
            JobEmbeddingProperties embeddingProperties
    ) {
        this.jobPostRepository = jobPostRepository;
        this.embeddingProperties = embeddingProperties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(embeddingProperties.getTimeoutMillis()))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<JobPost> searchJobs(SearchJobRequest request) {
        // 1. Chuẩn bị text tìm kiếm (nếu null thì để rỗng)
        String searchText = request.getText() != null ? request.getText().trim() : "";
        System.out.println("[SEARCH] Query text from FE: " + searchText);

        // 2. Chuyển đổi List<UUID> sang List<String> cho Python
        List<String> skillIdsStr = request.getSkillIds() != null
                ? request.getSkillIds()
                : new ArrayList<>();

        // 3. Đóng gói Filters đẩy thẳng sang Qdrant qua Python
        Map<String, Object> filters = new LinkedHashMap<>();
        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            filters.put("location", request.getLocation().trim());
        }
        if (request.getMinSalary() != null) {
            filters.put("min_salary_gte", request.getMinSalary());
        }
        if (request.getMaxSalary() != null) {
            filters.put("max_salary_lte", request.getMaxSalary());
        }

        // 4. Gọi Python API (Tái sử dụng endpoint Recommend nhưng với context là Search)
        List<String> rankedJobIds = callBertSearchApi(searchText, skillIdsStr, filters, 20);
        System.out.println("[SEARCH] Ranked Job IDs: " + rankedJobIds);

        if (rankedJobIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 5. Query DB và Map dữ liệu (Lưu ý: phải giữ nguyên thứ tự Ranking của AI)
        List<UUID> uuidList = rankedJobIds.stream().map(UUID::fromString).toList();
        List<JobPost> jobsFromDb = jobPostRepository.findAllById(uuidList);

        // Chuyển List thành Map để truy xuất O(1) theo ID
        Map<UUID, JobPost> jobMap = jobsFromDb.stream()
                .collect(Collectors.toMap(JobPost::getId, job -> job));

        // 6. Trả về list đã xếp hạng chuẩn xác
        return uuidList.stream()
                .map(jobMap::get)
                .filter(job -> job != null && !job.isDeleted()) // Lọc bỏ job null hoặc đã xóa mềm
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<String> callBertSearchApi(String text, List<String> skillIds, Map<String, Object> filters, int topK) {
        try {
            String url = embeddingProperties.getBaseUrl() + "/api/v1/search";
            Map<String, Object> payload = new LinkedHashMap<>();

            payload.put("keyword", text);   // field đúng với SearchRequest schema
            payload.put("top_k", topK);

            if (skillIds != null && !skillIds.isEmpty()) {
                payload.put("skill_ids", skillIds);
            }
            if (filters != null && !filters.isEmpty()) {
                payload.put("filters", filters);
            }

            String jsonPayload = objectMapper.writeValueAsString(payload);
            System.out.println("[SEARCH] Payload to bert_service: " + jsonPayload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMillis(embeddingProperties.getTimeoutMillis()))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("[SEARCH] Error from bert_service: " + response.body());
                return List.of();
            }

            Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> recObjects = (List<Map<String, Object>>) responseBody.get("recommendations");

            if (recObjects == null || recObjects.isEmpty()) {
                return List.of();
            }

            return recObjects.stream()
                    .map(m -> (String) m.get("original_id"))
                    .toList();

        } catch (Exception e) {
            System.err.println("[SEARCH] Error calling bert_service: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}