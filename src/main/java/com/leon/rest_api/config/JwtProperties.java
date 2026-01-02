package com.leon.rest_api.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    private String secret = null;
    private AccessToken accessToken = new AccessToken();
    private RefreshToken refreshToken = new RefreshToken();

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    @PostConstruct
    public void logConfig() {
        if (secret == null || secret.isBlank() || secret.length() < 34) {
            logger.error("JWT Secret is not correctly configured! Application cannot start. Please set the jwt.secret environment variable.");
            throw new IllegalStateException("JWT Secret is incorrect/missing from configuration.");
        }

        String maskedSecret = (secret != null && secret.length() >= 34) ? secret.substring(0, 6) + "..." : "[HIDDEN]";
        logger.info("JwtProperties loaded: secret={}, accessToken.expiration={}, refreshToken.expiration={}, refreshToken.cookieName={}, refreshToken.maxAge={}",
                maskedSecret,
                accessToken.getExpiration(),
                refreshToken.getExpiration(),
                refreshToken.getCookieName(),
                refreshToken.getMaxAge());
    }

    public static class AccessToken {
        private long expiration;

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }
    }

    public static class RefreshToken {
        private long expiration;
        private String cookieName;
        private int maxAge;

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }

        public String getCookieName() {
            return cookieName;
        }

        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }

        public int getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(int maxAge) {
            this.maxAge = maxAge;
        }
    }
}
