package com.leon.rest_api.service;

import com.leon.rest_api.config.AppProperties;
import com.leon.rest_api.config.TokenProperties;
import com.leon.rest_api.dto.TokenTuple;
import com.leon.rest_api.entities.RefreshToken;
import com.leon.rest_api.entities.User;
import com.leon.rest_api.exception.RefreshTokenException;
import com.leon.rest_api.repository.RefreshTokenRepository;
import com.leon.rest_api.repository.UserRepository;
import com.leon.rest_api.security.JwtTokenUtils;
import com.leon.rest_api.security.RefreshTokenUtils;
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

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProperties tokenProperties;
    private final JwtTokenUtils jwtTokenUtils;
    private final AppProperties appProperties;
    private final UserRepository userRepository;
    private final RefreshTokenUtils refreshTokenUtils;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               TokenProperties tokenProperties,
                               JwtTokenUtils jwtTokenUtils, AppProperties appProperties, UserRepository userRepository, RefreshTokenUtils refreshTokenUtils) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProperties = tokenProperties;
        this.jwtTokenUtils = jwtTokenUtils;
        this.appProperties = appProperties;
        this.userRepository = userRepository;
        this.refreshTokenUtils = refreshTokenUtils;
    }

    @Transactional
    public TokenTuple rotateRefreshToken(String refreshToken) {
        // 1. Find the token in DB
        String hashedRefreshToken = refreshTokenUtils.hashRefreshToken(refreshToken);
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByTokenHash(hashedRefreshToken)
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
        refreshTokenRepository.save(refreshTokenEntity);

        // 5. Get the user
        long userid = refreshTokenEntity.getUserId();
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new RefreshTokenException("User not found"));

        // 6. Generate NEW access token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        String accessToken = jwtTokenUtils.generateAccessToken(authentication);

        // 7. Generate NEW refresh token (ROTATION)
        String newRefreshToken = refreshTokenUtils.generateRandomRefreshToken(authentication);
        refreshTokenUtils.saveRefreshToken(user, newRefreshToken);

        return new TokenTuple(accessToken, newRefreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenUtils.revokeExpiredToken(refreshToken);
            throw new RefreshTokenException("Invalid Refresh Token");
        }
        return refreshToken;
    }

    public void verifyOldRevokedRefreshToken (RefreshToken refreshTokenEntity){
        if (refreshTokenEntity.isRevoked()){
            refreshTokenRepository.revokeAllRefreshToken("Suspect replayed attack",refreshTokenEntity.getUserId());
            log.warn("Suspect replayed attack: Refresh token has been revoked");
            throw new RefreshTokenException("Invalid Refresh Token");
        }
    }






    







}
