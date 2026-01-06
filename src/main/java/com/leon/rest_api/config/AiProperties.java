package com.leon.rest_api.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private static final Logger logger = LoggerFactory.getLogger(AiProperties.class);

    private String apiUrl = "http://localhost:3000/v1/chat/completions";
    private int maxToken = 20000;
    private double temp = 0.7;
    private String modelName = "qwen/qwen3-4b-2507";
    private boolean thinking = false;

    @PostConstruct
    public void logConfiguration() {
        logger.info("""
            === 🧠 LLM Configuration Initialized ===
            📡 API URL     : {}
            💬 Model Name  : {}
            🔢 Max Tokens  : {}
            🌡️ Temperature : {}
            🌡️ Thinking Model : {}
            ========================================
            """, apiUrl, modelName, maxToken, temp, thinking);
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public int getMaxToken() {
        return maxToken;
    }

    public void setMaxToken(int maxToken) {
        this.maxToken = maxToken;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public boolean isThinking() {
        return thinking;
    }

    public void setThinking(boolean thinking) {
        this.thinking = thinking;
    }
}
