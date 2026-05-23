package vn.edu.uit.socialjob.platform.modules.jobpost.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import vn.edu.uit.socialjob.platform.config.JobEmbeddingProperties;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobPostRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobSkillRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.net.http.HttpResponse;

@Service
public class JobEmbeddingClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;
    private final JobEmbeddingProperties properties;

    public JobEmbeddingClient(JobEmbeddingProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(properties.getTimeoutMillis()))
            .build();
    }

    public void sendEmbedding(UUID jobId, JobPostRequest request) {
        sendEmbedding(jobId, request, null);
    }

    public void sendEmbedding(UUID jobId, JobPostRequest request, List<JobSkillRequest> skills) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("id", jobId.toString());
            payload.put("text", buildEmbeddingText(request, skills));
            // include snapshot metadata (e.g., location) if available
            Map<String, Object> metadata = new LinkedHashMap<>();
            if (request.getLocation() != null) {
                metadata.put("location", request.getLocation());
            }
            // include skills snapshot if provided
            if (skills != null && !skills.isEmpty()) {
                List<String> skillIds = skills.stream()
                    .map(s -> s.getSkillId() != null ? s.getSkillId().toString() : null)
                    .filter(id -> id != null && !id.isBlank())
                    .toList();
                metadata.put("skill_ids", skillIds);
            }
            if (!metadata.isEmpty()) {
                payload.put("payload", metadata);
            }

            String body = objectMapper.writeValueAsString(payload);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(properties.getBaseUrl() + properties.getEmbedPath()))
                .timeout(Duration.ofMillis(properties.getTimeoutMillis()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Embed service returned status " + response.statusCode() + ": " + response.body());
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to send job embedding request", ex);
        }
    }

    public void sendMetadataUpdate(UUID jobId, Map<String, Object> metadata) {
        // Metadata update temporarily disabled (using original MMR only)
        System.out.println("[JobEmbeddingClient] sendMetadataUpdate called but is currently disabled (no-op)");
    }

    public void sendMetadataUpdateAfterCommit(UUID jobId, Map<String, Object> metadata) {
        // No-op while metadata updates are disabled
        System.out.println("[JobEmbeddingClient] sendMetadataUpdateAfterCommit called but is currently disabled (no-op)");
    }

    public void sendEmbeddingAfterCommit(UUID jobId, JobPostRequest request) {
        sendEmbeddingAfterCommit(jobId, request, null);
    }

    public void sendEmbeddingAfterCommit(UUID jobId, JobPostRequest request, List<JobSkillRequest> skills) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendEmbedding(jobId, request, skills);
                }
            });
            return;
        }

        sendEmbedding(jobId, request, skills);
    }

    private String buildEmbeddingText(JobPostRequest request, List<JobSkillRequest> skills) {
        StringBuilder builder = new StringBuilder();
        appendField(builder, "Title", request.getTitle());
        appendField(builder, "Description", request.getDescription());
        appendField(builder, "Experience level", request.getExperienceLevel() != null ? request.getExperienceLevel().name() : null);
        appendField(builder, "Location", request.getLocation());

        if (skills != null && !skills.isEmpty()) {
            for (JobSkillRequest skill : skills) {
                String skillLabel = skill.getSkillId() != null ? skill.getSkillId().toString() : skill.getSkillName();
                String skillInfo = skillLabel + (Boolean.TRUE.equals(skill.getRequired()) ? " (required)" : " (optional)");
                appendField(builder, "Skill", skillInfo);
            }
        }

        return builder.toString().trim();
    }

    private void appendField(StringBuilder builder, String label, String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        if (!builder.isEmpty()) {
            builder.append("\n");
        }
        builder.append(label).append(": ").append(value.trim());
    }
}