package com.leon.common.dto.response;

import com.leon.common.dto.request.LogoutRequestInterface;

import java.time.LocalDateTime;

public class LogoutResponse implements LogoutResponseInterface {
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
