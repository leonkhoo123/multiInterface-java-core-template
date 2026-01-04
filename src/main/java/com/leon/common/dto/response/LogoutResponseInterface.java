package com.leon.common.dto.response;

import java.time.LocalDateTime;

public interface LogoutResponseInterface {
    public String getUsername();

    public void setUsername(String username);

    public LocalDateTime getLogoutTime();

    public void setLogoutTime(LocalDateTime logoutTime);
}
