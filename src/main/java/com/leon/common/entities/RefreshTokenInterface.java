package com.leon.common.entities;

import java.time.Instant;
import java.time.LocalDateTime;

public interface RefreshTokenInterface {

    // Identity and Ownership
    Long getId();

    Long getUserId();
    void setUserId(Long userId);

    // Security and Identification
    String getTokenHash();
    void setTokenHash(String tokenHash);

    // Device and Metadata
    String getDeviceFingerprint();
    void setDeviceFingerprint(String deviceFingerprint);

    String getDeviceName();
    void setDeviceName(String deviceName);

    String getUserAgent();
    void setUserAgent(String userAgent);

    String getIpAddress();
    void setIpAddress(String ipAddress);

    // Lifecycle and Expiration
    Instant getExpiryDate();
    void setExpiryDate(Instant expiryDate);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getLastUsedAt();
    void setLastUsedAt(LocalDateTime lastUsedAt);

    // Status and Revocation
    boolean isRevoked();
    void setRevoked(boolean revoked);

    LocalDateTime getRevokedAt();
    void setRevokedAt(LocalDateTime revokedAt);

    String getRevokeReason();
    void setRevokeReason(String revokeReason);
}
