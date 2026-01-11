package com.leon.common.security;

import com.leon.common.config.AppProperties;
import com.leon.common.config.TokenProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class RefreshTokenUtils {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenUtils.class);

    private final TokenProperties tokenProperties;
    private final AppProperties appProperties;

    public RefreshTokenUtils(TokenProperties tokenProperties, AppProperties appProperties) {
        this.tokenProperties = tokenProperties;
        this.appProperties = appProperties;
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

    public ResponseCookie buildDeviceIdCookie(String deviceId, long maxAge) {
        return ResponseCookie.from(tokenProperties.getDeviceId().getCookieName(), deviceId != null ? deviceId : "")
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
            throw new RuntimeException("GenerateRandomRefreshToken failed!");
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> tokenProperties.getRefreshToken().getCookieName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public String extractDeviceIdFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> tokenProperties.getDeviceId().getCookieName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

}
