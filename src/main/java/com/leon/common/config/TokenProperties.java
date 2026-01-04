package com.leon.common.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "token")
public class TokenProperties {

    private static final Logger logger = LoggerFactory.getLogger(TokenProperties.class);

    private AccessToken accessToken = new AccessToken();
    private RefreshToken refreshToken = new RefreshToken();

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
        if (accessToken.secret == null || accessToken.secret.isBlank() || accessToken.secret.length() < 34) {
            logger.error("JWT Secret is not correctly configured! Application cannot start. Please set the token.accessToken.secret environment variable.");
            throw new IllegalStateException("JWT Secret is incorrect/missing from configuration.");
        }

        if (refreshToken.papper == null || refreshToken.papper.isBlank() || refreshToken.papper.length() < 34) {
            logger.error("Access Token Secret is not correctly configured! Application cannot start. Please set the token.refreshToken.papper environment variable.");
            throw new IllegalStateException("Refresh Token papper is incorrect/missing from configuration.");
        }

        String jwtmaskedSecret = (accessToken.secret != null && accessToken.secret.length() >= 34) ? accessToken.secret.substring(0, 6) + "..." : "[HIDDEN]";
        String refreshPapperMaskedSecret = (refreshToken.papper != null && refreshToken.papper.length() >= 34) ? refreshToken.papper.substring(0, 6) + "..." : "[HIDDEN]";

        logger.info("TokenProperties loaded: accessToken secret={}, accessToken.expiration={}",
                jwtmaskedSecret,
                accessToken.getExpiration());

        logger.info("TokenProperties loaded: refreshToken secret={}, refreshToken.expiration={}, refreshToken.cookieName={}, refreshToken.maxAge={}",
                refreshPapperMaskedSecret,
                refreshToken.getExpiration(),
                refreshToken.getCookieName(),
                refreshToken.getMaxAge());
    }

    public static class AccessToken {
        private String secret = null;

        private long expiration;

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    public static class RefreshToken {
        private String papper = null;
        private long expiration;
        private String cookieName ;
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

        public String getPapper() {
            return papper;
        }

        public void setPapper(String papper) {
            this.papper = papper;
        }
    }
}
