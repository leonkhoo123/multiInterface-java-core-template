package com.leon.common.controller;

import com.leon.common.config.TokenProperties;
import com.leon.common.dto.TokenTuple;
import com.leon.common.dto.request.LoginRequest;
import com.leon.common.dto.response.CommonResponse;
import com.leon.common.dto.response.LoginResponse;
import com.leon.common.dto.response.LogoutResponse;
import com.leon.common.security.JwtTokenUtils;
import com.leon.common.security.RefreshTokenUtils;
import com.leon.common.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAuthController implements UserAuthControllerInterface {

    private static final Logger log = LoggerFactory.getLogger(UserAuthController.class);

    private final UserAuthService userAuthService;
    private final TokenProperties tokenProperties;
    private final RefreshTokenUtils refreshTokenUtils;
    private final JwtTokenUtils jwtTokenUtils;

    public UserAuthController(UserAuthService userAuthService, TokenProperties tokenProperties, RefreshTokenUtils refreshTokenUtils, JwtTokenUtils jwtTokenUtils){
        this.userAuthService = userAuthService;
        this.tokenProperties = tokenProperties;
        this.refreshTokenUtils = refreshTokenUtils;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    public ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = userAuthService.login(request);

        // Set refresh token as HTTP-only cookie using ResponseCookie builder
        ResponseCookie refreshTokenCookie = refreshTokenUtils.buildRefreshTokenCookie(loginResponse.getRefreshToken(), tokenProperties.getRefreshToken().getMaxAge());

        // Set device id as HTTP-only cookie
        ResponseCookie deviceIdCookie = refreshTokenUtils.buildDeviceIdCookie(request.getDeviceId(), tokenProperties.getDeviceId().getMaxAge());

        // Remove refresh token from response body for security
        loginResponse.setRefreshToken(null);
        loginResponse.setDeviceId(null);

        log.info("Login successful");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, deviceIdCookie.toString())
                .body(CommonResponse.success("Login successful", loginResponse));
    }

    @Override
    public ResponseEntity<CommonResponse<LogoutResponse>> logout(HttpServletRequest request) {
        String accessToken = jwtTokenUtils.getJwtFromRequest(request);
        String deviceId = refreshTokenUtils.extractDeviceIdFromCookie(request);
        String refreshToken = refreshTokenUtils.extractRefreshTokenFromCookie(request);
        TokenTuple tokens = new TokenTuple(accessToken,refreshToken,deviceId);
        LogoutResponse logoutResponse = userAuthService.logout(tokens);

        // Clear refresh token cookie
        ResponseCookie refreshTokenCookie = refreshTokenUtils.buildRefreshTokenCookie("", 0);
        // Clear device id cookie
        ResponseCookie deviceIdCookie = refreshTokenUtils.buildDeviceIdCookie("", 0);

        log.info("Logout successful");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, deviceIdCookie.toString())
                .body(CommonResponse.success("Logout successful", logoutResponse));
    }


}
