package com.leon.rest_api.security;

import com.leon.rest_api.config.AppProperties;
import com.leon.rest_api.config.TokenProperties;
import com.leon.rest_api.controller.RefreshTokenController;
import com.leon.rest_api.entities.RefreshToken;
import com.leon.rest_api.entities.User;
import com.leon.rest_api.exception.RefreshTokenException;
import com.leon.rest_api.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class RefreshTokenUtils {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenUtils.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProperties tokenProperties;
    private final AppProperties appProperties;

    public RefreshTokenUtils(RefreshTokenRepository refreshTokenRepository, TokenProperties tokenProperties, AppProperties appProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProperties = tokenProperties;
        this.appProperties = appProperties;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeExpiredToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshToken.setRevokeReason("Expired");
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }

    public void saveRefreshToken(User user, String refreshToken) {
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUserId(user.getId());
        refreshTokenEntity.setTokenHash(hashRefreshToken(refreshToken));
        refreshTokenEntity.setLastUsedAt(LocalDateTime.now());
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenEntity.setExpiryDate(Instant.now().plusMillis(tokenProperties.getRefreshToken().getExpiration()));

        refreshTokenRepository.save(refreshTokenEntity);
    }

    public ResponseCookie buildRefreshTokenCookie(String refreshToken, long maxAge) {
        return ResponseCookie.from(tokenProperties.getRefreshToken().getCookieName(), refreshToken != null ? refreshToken : "")
                .httpOnly(true)
                .secure(appProperties.isSecureCookie()) // Must be true in prod for HTTPS
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict") // Protects against CSRF
                .build();
    }

    public String hashRefreshToken(String token) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    tokenProperties.getRefreshToken().getPapper().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            ));
            byte[] hash = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Token hashing failed", e);
        }
    }

    public String generateRandomRefreshToken(Authentication authentication) {
        byte[] randomBytes = new byte[64];
        try{
            SecureRandom.getInstanceStrong().nextBytes(randomBytes);
        }catch (Exception e){
            throw new RuntimeException("generateRandomRefreshToken broken");
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public long getRefreshTokenExpiration() {
        return tokenProperties.getRefreshToken().getExpiration();
    }

    public void logoutUser(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshToken.setRevokeReason("Logout");
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> tokenProperties.getRefreshToken().getCookieName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

}
