package vn.edu.uit.socialjob.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "job.embedding")
public class JobEmbeddingProperties {

    private String baseUrl = "http://localhost:8000";

    private String embedPath = "/api/v1/jobs/embed";

    private long timeoutMillis = 10000;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getEmbedPath() {
        return embedPath;
    }

    public void setEmbedPath(String embedPath) {
        this.embedPath = embedPath;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
}