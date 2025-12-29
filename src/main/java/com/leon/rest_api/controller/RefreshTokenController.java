package com.leon.rest_api.controller;

import com.leon.rest_api.dto.response.CommonResponse;
import com.leon.rest_api.dto.response.RefreshTokenResponse;
import com.leon.rest_api.service.AuthService;
import com.leon.rest_api.dto.TokenTuple; // You'll need this simple POJO
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
public class RefreshTokenController {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenController.class);

    private final AuthService authService;

    @Value("${jwt.refresh-token.cookie-name:refresh_token}")
    private String refreshTokenCookieName;

    @Value("${jwt.refresh-token.max-age:604800}")
    private int refreshTokenMaxAge;

    @Value("${app.secure-cookie:true}")
    private boolean secureCookie;

    public RefreshTokenController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<RefreshTokenResponse>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        String oldRefreshToken = extractRefreshTokenFromCookie(request);

        if (oldRefreshToken == null || oldRefreshToken.isEmpty()) {
            log.warn("Refresh attempt without cookie");
            return ResponseEntity.badRequest().body(CommonResponse.failure("Refresh token missing", "MISSING_TOKEN"));
        }

        // 1. Rotate tokens via Service
        TokenTuple tokens = authService.rotateRefreshToken(oldRefreshToken);

        // 2. Set the NEW refresh token in the HTTP-Only cookie
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, tokens.getRefreshToken())
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(refreshTokenMaxAge)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 3. Return only the new Access Token in the body
        RefreshTokenResponse refreshResponse = new RefreshTokenResponse(
                tokens.getAccessToken(),
                "Bearer",
                refreshTokenMaxAge // Or your access token duration
        );

        return ResponseEntity.ok(CommonResponse.success("Token refreshed successfully", refreshResponse));
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> refreshTokenCookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}