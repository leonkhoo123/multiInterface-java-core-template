package com.leon.common.controller;

import com.leon.common.config.AppProperties;
import com.leon.common.config.TokenProperties;
import com.leon.common.dto.TokenTuple;
import com.leon.common.dto.response.CommonResponse;
import com.leon.common.dto.response.RefreshTokenResponse;
import com.leon.common.security.RefreshTokenUtils;
import com.leon.common.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
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
        String deviceId = refreshTokenUtils.extractDeviceIdFromCookie(request);

        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("Refresh attempt without cookie");
            return ResponseEntity.badRequest().body(CommonResponse.failure("Refresh token missing", "MISSING_TOKEN"));
        }

        if (deviceId == null || deviceId.isEmpty()) {
            log.warn("Refresh attempt without deviceId");
            return ResponseEntity.badRequest().body(CommonResponse.failure("Device Id missing", "MISSING_DEVICE_ID"));
        }

        TokenTuple input = new TokenTuple("",refreshToken,deviceId);
        // 1. Rotate tokens via Service
        TokenTuple tokenTuple = refreshTokenService.rotateRefreshToken(input);

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
                tokenProperties.getAccessToken().getExpiration(), // Use access token expiration here
                tokenTuple.getDeviceId()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(CommonResponse.success("Token refreshed successfully", response));
    }


}
