package com.leon.common.dto;


public class TokenTuple {
    private String accessToken;
    private String refreshToken;
    private String deviceId;

    public TokenTuple() {
    }

    public TokenTuple(String accessToken, String refreshToke, String deviceId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToke;
        this.deviceId = deviceId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}