package vn.edu.uit.socialjob.platform.modules.apply.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.config.BertServiceProperties;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BertServiceClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;
    private final BertServiceProperties properties;

    public BertServiceClient(BertServiceProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(properties.getTimeoutMillis()))
            .build();
    }

    /**
     * Score CV using BERT service
     * @param cvFileUrl URL of the CV file
     * @param userSkillIds List of user's skill IDs
     * @param jobSkillIds List of job required skill IDs
     * @return Score between 0-10
     */
    public Double scoreCv(String cvFileUrl,String jobId, List<String> userSkillIds, List<String> jobSkillIds) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("cv_url", cvFileUrl);
            payload.put("job_id", jobId);
            payload.put("user_skills", userSkillIds != null ? userSkillIds : List.of());
            payload.put("job_skills", jobSkillIds != null ? jobSkillIds : List.of());

            String body = objectMapper.writeValueAsString(payload);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(properties.getBaseUrl() + properties.getScorePath()))
                .timeout(Duration.ofMillis(properties.getTimeoutMillis()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("BERT service returned status " + response.statusCode() + ": " + response.body());
            }

            // Parse response to get score
            Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
            Object scoreValue = responseMap.get("score");
            
            if (scoreValue == null) {
                throw new IllegalStateException("BERT service response does not contain score");
            }

            // Convert to Double (handle both Integer and Double responses)
            if (scoreValue instanceof Number) {
                return ((Number) scoreValue).doubleValue();
            }
            
            return Double.parseDouble(scoreValue.toString());

        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to score CV with BERT service", ex);
        }
    }
}
