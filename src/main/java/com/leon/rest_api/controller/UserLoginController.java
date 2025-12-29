package com.leon.rest_api.controller;

import com.leon.rest_api.dto.request.LoginRequest;
import com.leon.rest_api.dto.response.CommonResponse;
import com.leon.rest_api.dto.response.LoginResponse;
import com.leon.rest_api.dto.response.LogoutResponse;
import com.leon.rest_api.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserLoginController implements UserLoginControllerInterface {

    @Autowired
    private final AuthService authService;

    @Value("${jwt.refresh-token.cookie-name:refresh_token}")
    private String refreshTokenCookieName;

    @Value("${jwt.refresh-token.max-age:604800}") // 7 days default
    private int refreshTokenMaxAge;

    @Value("${app.secure-cookie:true}")
    private boolean secureCookie;

    public UserLoginController(AuthService authService){
        this.authService = authService;
    };

    @Override
    public ResponseEntity<CommonResponse<LoginResponse>> login(LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);

        // Set refresh token as HTTP-only cookie
        Cookie refreshTokenCookie = createRefreshTokenCookie(loginResponse.getRefreshToken());
        response.addCookie(refreshTokenCookie);

        // Remove refresh token from response body for security
        loginResponse.setRefreshToken(null);

        return ResponseEntity.ok(CommonResponse.success("Login successful",loginResponse));
    }

    @Override
    public ResponseEntity<CommonResponse<LogoutResponse>> logout(String authorization, HttpServletResponse response) {
        String token = extractToken(authorization);
        LogoutResponse logoutResponse = authService.logout(token);

        // Clear refresh token cookie
        Cookie clearCookie = createClearedRefreshTokenCookie();
        response.addCookie(clearCookie);

        return ResponseEntity.ok(CommonResponse.success("Logout successful",logoutResponse));
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(refreshTokenCookieName, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie); // true in production (HTTPS only)
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenMaxAge);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .httpOnly(true)
                .secure(secureCookie) // Must be true in prod for HTTPS
                .path("/")
                .maxAge(refreshTokenMaxAge)
                .sameSite("Strict") // Protects against CSRF
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private Cookie createClearedRefreshTokenCookie() {
        Cookie cookie = new Cookie(refreshTokenCookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        throw new IllegalArgumentException("Invalid authorization header");
    }
}