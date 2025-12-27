package com.leon.rest_api.dto.request;

public record LogoutRequest(String username, String accessToken, String refreshToken) {}
