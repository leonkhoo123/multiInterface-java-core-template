package com.leon.common.entities;

import java.time.Instant;

public interface BlacklistedTokenInterface {
    public String getToken();
    public void setToken(String token);
    public Instant getExpiryDate();
    public void setExpiryDate(Instant expiryDate);
}
