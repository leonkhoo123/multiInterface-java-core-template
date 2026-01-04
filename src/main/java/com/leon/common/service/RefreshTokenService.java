package com.leon.common.service;

import com.leon.common.dto.TokenTuple;
import com.leon.common.entities.RefreshTokenInterface;
import com.leon.common.entities.UserInterface;
import com.leon.common.exception.RefreshTokenException;
import com.leon.common.repository.RefreshTokenStore;
import com.leon.common.repository.UserStore;
import com.leon.common.security.JwtTokenUtils;
import com.leon.common.security.RefreshTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenUtils refreshTokenUtils;
    private final UserStore userStore;
    private final RefreshTokenStore refreshTokenStore;

    public RefreshTokenService(JwtTokenUtils jwtTokenUtils, RefreshTokenUtils refreshTokenUtils,
                               UserStore userStore,
                               RefreshTokenStore refreshTokenStore) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.refreshTokenUtils = refreshTokenUtils;
        this.userStore = userStore;
        this.refreshTokenStore = refreshTokenStore;
    }

    @Transactional
    public TokenTuple rotateRefreshToken(String refreshToken) {
        // 1. Find the token in DB
        String hashedRefreshToken = refreshTokenUtils.hashRefreshToken(refreshToken);
        RefreshTokenInterface refreshTokenEntity = refreshTokenStore.findByTokenHash(hashedRefreshToken)
                .orElseThrow(() -> new RefreshTokenException("Invalid Refresh Token"));

        // 2. Verify expiration
        verifyOldRevokedRefreshToken(refreshTokenEntity);

        // 3. Verify expiration
        verifyExpiration(refreshTokenEntity);

        // 4. current valid refresh token is revoked, before assign new refresh token
        refreshTokenEntity.setRevoked(true);
        refreshTokenEntity.setRevokeReason("Rotated");
        refreshTokenEntity.setRevokedAt(LocalDateTime.now());
        refreshTokenEntity.setLastUsedAt(LocalDateTime.now());
        refreshTokenStore.save(refreshTokenEntity);

        // 5. Get the user
        long userid = refreshTokenEntity.getUserId();
        UserInterface user = userStore.findById(userid)
                .orElseThrow(() -> new RefreshTokenException("User not found"));

        // 6. Generate NEW access token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        String accessToken = jwtTokenUtils.generateAccessToken(authentication);

        // 7. Generate NEW refresh token (ROTATION)
        String newRefreshToken = refreshTokenUtils.generateRandomRefreshToken(authentication);
        refreshTokenStore.saveRefreshToken(user, newRefreshToken);

        return new TokenTuple(accessToken, newRefreshToken);
    }

    public RefreshTokenInterface verifyExpiration(RefreshTokenInterface refreshToken) {
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenStore.revokeExpiredToken(refreshToken);
            throw new RefreshTokenException("Invalid Refresh Token");
        }
        return refreshToken;
    }

    public void verifyOldRevokedRefreshToken (RefreshTokenInterface refreshTokenEntity){
        if (refreshTokenEntity.isRevoked()){
            refreshTokenStore.revokeAllRefreshToken("Suspect replayed attack",refreshTokenEntity.getUserId());
            log.warn("Suspect replayed attack: Refresh token has been revoked");
            throw new RefreshTokenException("Invalid Refresh Token");
        }
    }






    







}
