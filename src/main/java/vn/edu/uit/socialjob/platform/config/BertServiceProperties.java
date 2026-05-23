package vn.edu.uit.socialjob.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bert.service")
public class BertServiceProperties {

    private String baseUrl = "http://localhost:8001";

    private String scorePath = "/api/v1/score-cv";

    private long timeoutMillis = 30000;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getScorePath() {
        return scorePath;
    }

    public void setScorePath(String scorePath) {
        this.scorePath = scorePath;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
}
