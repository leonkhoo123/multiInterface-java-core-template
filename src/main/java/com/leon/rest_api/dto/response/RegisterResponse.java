package com.leon.rest_api.dto.response;

import java.time.LocalDateTime;

public class RegisterResponse {
    private String username;
    private String email;
    private LocalDateTime registeredAt;

    public RegisterResponse(String username, String email, LocalDateTime registeredAt) {
        this.username = username;
        this.email = email;
        this.registeredAt = registeredAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}