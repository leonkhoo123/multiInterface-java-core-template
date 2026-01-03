package com.leon.rest_api.controller;

import com.leon.rest_api.config.TokenProperties;
import com.leon.rest_api.dto.TokenTuple;
import com.leon.rest_api.dto.request.LoginRequest;
import com.leon.rest_api.dto.response.CommonResponse;
import com.leon.rest_api.dto.response.LoginResponse;
import com.leon.rest_api.dto.response.LogoutResponse;
import com.leon.rest_api.security.JwtTokenUtils;
import com.leon.rest_api.security.RefreshTokenUtils;
import com.leon.rest_api.service.UserAuthService;
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

        // Remove refresh token from response body for security
        loginResponse.setRefreshToken(null);

        log.info("Login successful");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(CommonResponse.success("Login successful", loginResponse));
    }

    @Override
    public ResponseEntity<CommonResponse<LogoutResponse>> logout(HttpServletRequest request) {
        String accessToken = jwtTokenUtils.getJwtFromRequest(request);
        String refreshToken = refreshTokenUtils.extractRefreshTokenFromCookie(request);
        TokenTuple tokens = new TokenTuple(accessToken,refreshToken);
        LogoutResponse logoutResponse = userAuthService.logout(tokens);

        // Clear refresh token cookie
        ResponseCookie refreshTokenCookie = refreshTokenUtils.buildRefreshTokenCookie("", 0);

        log.info("Logout successful");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(CommonResponse.success("Logout successful", logoutResponse));
    }


}
