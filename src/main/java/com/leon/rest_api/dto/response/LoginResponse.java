package com.leon.rest_api.dto.response;

import java.util.Set;

public class LoginResponse {

    private String accessToken;
    private String refreshToken; // Note: Controller will null this before sending if using HttpOnly cookies
    private String tokenType;
    private long expiresIn;
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;

    // Default Constructor
    public LoginResponse() {
        this.tokenType = "Bearer";
    }

    // Full Constructor
    public LoginResponse(String accessToken, String refreshToken, String tokenType,
                         long expiresIn, Long id, String username,
                         String email, Set<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.expiresIn = expiresIn;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}