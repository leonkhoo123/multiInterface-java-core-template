package com.leon.common.dto.request;

public interface LogoutRequestInterface {
    public String getUsername();

    public void setUsername(String username);

    public String getAccessToken();

    public void setAccessToken(String accessToken);

    public String getRefreshToken();

    public void setRefreshToken(String refreshToken);
}
