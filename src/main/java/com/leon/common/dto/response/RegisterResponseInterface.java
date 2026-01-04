package com.leon.common.dto.response;

import java.time.LocalDateTime;

public interface RegisterResponseInterface {
    public String getUsername();

    public void setUsername(String username);

    public String getEmail();

    public void setEmail(String email);

    public LocalDateTime getRegisteredAt();

    public void setRegisteredAt(LocalDateTime registeredAt);
}
