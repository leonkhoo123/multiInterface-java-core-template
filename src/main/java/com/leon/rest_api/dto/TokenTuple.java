package com.leon.rest_api.dto;


public class TokenTuple {
    private String accessToken;
    private String refreshToken;

    public TokenTuple() {
    }

    public TokenTuple(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}