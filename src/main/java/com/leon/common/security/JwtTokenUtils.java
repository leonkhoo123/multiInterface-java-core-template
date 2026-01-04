package com.leon.common.security;

import com.leon.common.config.TokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenUtils {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtils.class);
    private static final String REQUEST_ID_KEY = "requestId";

    private final SecretKey key;
    private final TokenProperties tokenProperties;
    private final TokenBlacklistService blacklistService;

    public JwtTokenUtils(TokenProperties tokenProperties, TokenBlacklistService blacklistService) {
        this.tokenProperties = tokenProperties;
        this.key = Keys.hmacShaKeyFor(tokenProperties.getAccessToken().getSecret().getBytes(StandardCharsets.UTF_8));
        this.blacklistService = blacklistService;
    }

    public String generateAccessToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenProperties.getAccessToken().getExpiration());

        // Note: signWith(key) automatically detects HS512 based on key length
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Claims validateAndGetClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            logErrorWithRequestId(e);
        }
        return null;
    }

    private void logErrorWithRequestId(Exception e) {
        String requestId = MDC.get(REQUEST_ID_KEY);
        if (requestId == null) {
            requestId = "unknown";
        }
        log.error("[{}] JWT Validation failed: {}", requestId, e.getMessage());
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload() // .getBody() is now .getPayload()
                .getSubject();
    }

    public long getAccessTokenExpiration() {
        return tokenProperties.getAccessToken().getExpiration();
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistService.isBlacklisted(token);
    }

    public void blacklistToken(String token) {
        // Get remaining time of the token to set TTL in Redis
        long expirationTime = getExpirationDateFromToken(token).getTime();
        long ttl = expirationTime - System.currentTimeMillis();

        if (ttl > 0) {
            blacklistService.blacklist(token, ttl);
        }
    }

    private Date getExpirationDateFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public String generateRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenProperties.getRefreshToken().getExpiration());

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key) // Uses the same SecretKey
                .compact();
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
