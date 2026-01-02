package com.leon.rest_api.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private static final Logger logger = LoggerFactory.getLogger(AppProperties.class);

    private boolean secureCookie = true;

    public boolean isSecureCookie() {
        return secureCookie;
    }

    public void setSecureCookie(boolean secureCookie) {
        this.secureCookie = secureCookie;
    }

    @PostConstruct
    public void logConfig() {
        logger.info("AppProperties loaded: secureCookie={}", secureCookie);
    }
}
