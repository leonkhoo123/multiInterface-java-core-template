package com.leon.common.dto.response;

public class RefreshTokenResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String deviceId;
    // Standard Default Constructor
    public RefreshTokenResponse() {
        this.tokenType = "Bearer"; // Default value
    }

    // All-Arguments Constructor
    public RefreshTokenResponse(String accessToken, String tokenType, long expiresIn, String deviceId) {
        this.accessToken = accessToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.expiresIn = expiresIn;
        this.deviceId = deviceId;

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
}