package com.leon.rest_api.controller;

import com.leon.rest_api.config.AppProperties;
import com.leon.rest_api.config.TokenProperties;
import com.leon.rest_api.dto.response.CommonResponse;
import com.leon.rest_api.dto.response.RefreshTokenResponse;
import com.leon.rest_api.security.RefreshTokenUtils;
import com.leon.rest_api.service.RefreshTokenService;
import com.leon.rest_api.dto.TokenTuple;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/")
public class RefreshTokenController {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenController.class);

    private final RefreshTokenService refreshTokenService;
    private final TokenProperties tokenProperties;
    private final AppProperties appProperties;
    private final RefreshTokenUtils refreshTokenUtils;

    public RefreshTokenController(RefreshTokenService refreshTokenService, TokenProperties tokenProperties, AppProperties appProperties, RefreshTokenUtils refreshTokenUtils) {
        this.refreshTokenService = refreshTokenService;
        this.tokenProperties = tokenProperties;
        this.appProperties = appProperties;
        this.refreshTokenUtils = refreshTokenUtils;
    }

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<RefreshTokenResponse>> refreshToken(
            HttpServletRequest request) {

        String refreshToken = refreshTokenUtils.extractRefreshTokenFromCookie(request);

        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("Refresh attempt without cookie");
            return ResponseEntity.badRequest().body(CommonResponse.failure("Refresh token missing", "MISSING_TOKEN"));
        }

        // 1. Rotate tokens via Service
        TokenTuple tokenTuple = refreshTokenService.rotateRefreshToken(refreshToken);

        // 2. Set the NEW refresh token in the HTTP-Only cookie
        ResponseCookie refreshTokenCookie = ResponseCookie.from(tokenProperties.getRefreshToken().getCookieName(), tokenTuple.getRefreshToken())
                .httpOnly(true)
                .secure(appProperties.isSecureCookie())
                .path("/")
                .maxAge(tokenProperties.getRefreshToken().getMaxAge())
                .sameSite("Strict")
                .build();

        // 3. Return only the new Access Token in the body
        RefreshTokenResponse response = new RefreshTokenResponse(
                tokenTuple.getAccessToken(),
                "Bearer",
                tokenProperties.getAccessToken().getExpiration() // Use access token expiration here
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(CommonResponse.success("Token refreshed successfully", response));
    }


}
