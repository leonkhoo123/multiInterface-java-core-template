package com.leon.common.dto.response;

import java.util.Set;

public interface LoginResponseInterface {
    // Getters and Setters
    public String getAccessToken();
    public void setAccessToken(String accessToken);

    public String getRefreshToken();
    public void setRefreshToken(String refreshToken);

    public String getTokenType();
    public void setTokenType(String tokenType);

    public long getExpiresIn();
    public void setExpiresIn(long expiresIn);

    public Long getId();
    public void setId(Long id);

    public String getUsername();
    public void setUsername(String username);

    public String getEmail();
    public void setEmail(String email);

    public Set<String> getRoles();
    public void setRoles(Set<String> roles);

}
