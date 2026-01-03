package com.leon.rest_api.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private static final Logger logger = LoggerFactory.getLogger(AppProperties.class);

    private List<String> allowedOrigins = new ArrayList<>();;

    private boolean secureCookie = true;

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public boolean isSecureCookie() {
        return secureCookie;
    }

    public void setSecureCookie(boolean secureCookie) {
        this.secureCookie = secureCookie;
    }

    @PostConstruct
    public void logConfig() {
        logger.info("AppProperties loaded: secureCookie={}, allowedOrigins={}", secureCookie, allowedOrigins);
    }
}
