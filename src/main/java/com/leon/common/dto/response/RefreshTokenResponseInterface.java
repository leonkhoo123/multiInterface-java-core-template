package com.leon.common.dto.response;

public interface RefreshTokenResponseInterface {
    public String getAccessToken();

    public String getTokenType();

    public long getExpiresIn();

    // Setters
    public void setAccessToken(String accessToken);

    public void setTokenType(String tokenType);

    public void setExpiresIn(long expiresIn);
}
