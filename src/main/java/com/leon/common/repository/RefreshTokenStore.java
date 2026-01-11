package com.leon.common.repository;

import com.leon.common.entities.RefreshToken;
import com.leon.common.entities.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenStore {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeExpiredToken(RefreshToken refreshToken);

    public void saveRefreshToken(User user, String refreshToken, String userAgent, String ipAddress, String deviceId);

    public void logoutUser(RefreshToken refreshToken);
    public void revokeAllRefreshToken(String message, long id);

    public void revokeRefreshTokenByDeviceId(String message, long id, String deviceId);

    public Optional<RefreshToken> findByTokenHashAndDeviceId(String hashedToken, String deviceId);

    public void save(RefreshToken refreshToken);
}
