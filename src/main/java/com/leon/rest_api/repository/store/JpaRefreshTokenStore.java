package com.leon.rest_api.repository.store;

import com.leon.common.config.TokenProperties;
import com.leon.common.entities.RefreshToken;
import com.leon.common.entities.RefreshTokenInterface;
import com.leon.common.entities.UserInterface;
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

    public void revokeExpiredToken(RefreshTokenInterface refreshToken){
        refreshToken.setRevoked(true);
        refreshToken.setRevokeReason("Expired");
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save((RefreshToken) refreshToken);
    }

    public void saveRefreshToken(UserInterface user, String refreshToken){
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUserId(user.getId());
        refreshTokenEntity.setTokenHash(refreshTokenUtils.hashRefreshToken(refreshToken));
        refreshTokenEntity.setLastUsedAt(LocalDateTime.now());
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenEntity.setExpiryDate(Instant.now().plusMillis(tokenProperties.getRefreshToken().getExpiration()));

        refreshTokenRepository.save(refreshTokenEntity);
    }

    public void logoutUser(RefreshTokenInterface refreshToken){
        refreshToken.setRevoked(true);
        refreshToken.setRevokeReason("Logout");
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save((RefreshToken) refreshToken);
    }

    public void revokeAllRefreshToken(String message, long id){
        refreshTokenRepository.revokeAllRefreshToken(message,id);
    }

    public Optional<RefreshTokenInterface> findByTokenHash(String hashedToken){
        return refreshTokenRepository.findByTokenHash(hashedToken);
    }

    public void save(RefreshTokenInterface refreshToken){
        refreshTokenRepository.save((RefreshToken)  refreshToken);
    }
}
