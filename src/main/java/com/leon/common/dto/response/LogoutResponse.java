package com.leon.common.dto.response;

import java.time.LocalDateTime;

public class LogoutResponse {
    String username;
    LocalDateTime logoutTime;

    public LogoutResponse(String username, LocalDateTime now) {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }
}
