package com.leon.rest_api.repository.store;

import com.leon.common.config.TokenProperties;
import com.leon.common.entities.RefreshToken;
import com.leon.common.entities.User;
import com.leon.common.repository.RefreshTokenStore;
import com.leon.common.security.RefreshTokenUtils;
import com.leon.rest_api.repository.RefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class JpaRefreshTokenStore implements RefreshTokenStore {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenUtils refreshTokenUtils;
    private final TokenProperties tokenProperties;

    public JpaRefreshTokenStore(RefreshTokenRepository refreshTokenRepository, RefreshTokenUtils refreshTokenUtils, TokenProperties tokenProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenUtils = refreshTokenUtils;
        this.tokenProperties = tokenProperties;
    }

    public void revokeExpiredToken(RefreshToken refreshToken){
        refreshToken.setRevoked(true);
        refreshToken.setRevokeReason("Expired");
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }

    public void saveRefreshToken(User user, String refreshToken, String userAgent, String ipAddress, String deviceId){
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUserId(user.getId());
        refreshTokenEntity.setTokenHash(refreshTokenUtils.hashRefreshToken(refreshToken));
        refreshTokenEntity.setLastUsedAt(LocalDateTime.now());
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenEntity.setExpiryDate(Instant.now().plusMillis(tokenProperties.getRefreshToken().getExpiration()));
        refreshTokenEntity.setUserAgent(userAgent);
        refreshTokenEntity.setIpAddress(ipAddress);
        refreshTokenEntity.setDeviceId(deviceId);

        refreshTokenRepository.save(refreshTokenEntity);
    }

    public void logoutUser(RefreshToken refreshToken){
        refreshToken.setRevoked(true);
        refreshToken.setRevokeReason("Logout");
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }

    public void revokeAllRefreshToken(String message, long userId){
        refreshTokenRepository.revokeAllRefreshToken(message,userId);
    }

    @Override
    public void revokeRefreshTokenByDeviceId(String message, long userId, String deviceId) {
        refreshTokenRepository.revokeRefreshTokenByDeviceId(message,userId,deviceId);
    }

    @Override
    public Optional<RefreshToken> findByTokenHashAndDeviceId(String hashedToken, String deviceId) {
        return refreshTokenRepository.findByActiveTokenHashAndDeviceId(hashedToken,deviceId);
    }

    public Optional<RefreshToken> findByTokenHash(String hashedToken){
        return refreshTokenRepository.findByTokenHash(hashedToken);
    }

    public void save(RefreshToken refreshToken){
        refreshTokenRepository.save(refreshToken);
    }
}
