package com.leon.rest_api.security;

import com.leon.rest_api.service.TokenBlacklistService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey key;
    private final long jwtExpirationMs;
    private final TokenBlacklistService blacklistService; // Inject this

    @Value("${jwt.refreshExpirationMs:604800000}") // Default 7 days
    private static long refreshExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expirationMs:900000}") long jwtExpirationMs, TokenBlacklistService blacklistService) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
        this.blacklistService = blacklistService;
    }

    public String generateAccessToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Note: signWith(key) automatically detects HS512 based on key length
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            // New 0.12.x syntax: Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT Validation failed: {}", e.getMessage());
        }
        return false;
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
        return jwtExpirationMs;
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
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key) // Uses the same SecretKey
                .compact();
    }

    public long getRefreshTokenExpiration() {
        return refreshExpirationMs;
    }
}