package com.leon.rest_api.dto.response;

public class RefreshTokenResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;

    // Standard Default Constructor
    public RefreshTokenResponse() {
        this.tokenType = "Bearer"; // Default value
    }

    // All-Arguments Constructor
    public RefreshTokenResponse(String accessToken, String tokenType, long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.expiresIn = expiresIn;
    }

    // Getters
    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    // Setters
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    // Static Builder Method
    public static Builder builder() {
        return new Builder();
    }

    // Manual Builder Inner Class
    public static class Builder {
        private String accessToken;
        private String tokenType = "Bearer";
        private long expiresIn;

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder expiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public RefreshTokenResponse build() {
            return new RefreshTokenResponse(accessToken, tokenType, expiresIn);
        }
    }
}