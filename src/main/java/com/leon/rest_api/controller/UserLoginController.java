package com.leon.rest_api.controller;

import com.leon.rest_api.config.AppProperties;
import com.leon.rest_api.config.JwtProperties;
import com.leon.rest_api.dto.request.LoginRequest;
import com.leon.rest_api.dto.response.CommonResponse;
import com.leon.rest_api.dto.response.LoginResponse;
import com.leon.rest_api.dto.response.LogoutResponse;
import com.leon.rest_api.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserLoginController implements UserLoginControllerInterface {

    private static final Logger log = LoggerFactory.getLogger(UserLoginController.class);

    private final AuthService authService;
    private final JwtProperties jwtProperties;
    private final AppProperties appProperties;

    public UserLoginController(AuthService authService, JwtProperties jwtProperties, AppProperties appProperties){
        this.authService = authService;
        this.jwtProperties = jwtProperties;
        this.appProperties = appProperties;
    }

    @Override
    public ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);

        // Set refresh token as HTTP-only cookie using ResponseCookie builder
        ResponseCookie refreshTokenCookie = buildRefreshTokenCookie(loginResponse.getRefreshToken(), jwtProperties.getRefreshToken().getMaxAge());

        // Remove refresh token from response body for security
        loginResponse.setRefreshToken(null);

        log.info("Login successful");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(CommonResponse.success("Login successful", loginResponse));
    }

    @Override
    public ResponseEntity<CommonResponse<LogoutResponse>> logout(String authorization) {
        String accessToken = extractAccessToken(authorization);
        LogoutResponse logoutResponse = authService.logout(accessToken);

        // Clear refresh token cookie
        ResponseCookie refreshTokenCookie = buildRefreshTokenCookie("", 0);

        log.info("Logout successful");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(CommonResponse.success("Logout successful", logoutResponse));
    }

    private ResponseCookie buildRefreshTokenCookie(String refreshToken, long maxAge) {
        return ResponseCookie.from(jwtProperties.getRefreshToken().getCookieName(), refreshToken != null ? refreshToken : "")
                .httpOnly(true)
                .secure(appProperties.isSecureCookie()) // Must be true in prod for HTTPS
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict") // Protects against CSRF
                .build();
    }

    private String extractAccessToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        throw new IllegalArgumentException("Invalid authorization header");
    }
}
