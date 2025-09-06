package com.leon.rest_api.controller;

import com.leon.rest_api.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    // Simple in-memory store for refresh tokens (replace with DB in prod)
    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    /**
     * Login endpoint
     * Expects { "username": "xxx", "password": "yyy" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // TODO: validate user from DB
        if (!"leon".equals(username) || !"123456".equals(password)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }

        String accessToken = jwtUtils.generateJwtToken(username);
        String refreshToken = jwtUtils.generateRefreshToken(username);

        // Save refresh token
        refreshTokenStore.put(username, refreshToken);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

    /**
     * Refresh endpoint
     * Expects { "refreshToken": "xxx" }
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || !jwtUtils.validateJwtToken(refreshToken)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token"));
        }

        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
        String storedToken = refreshTokenStore.get(username);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token not recognized"));
        }

        String newAccessToken = jwtUtils.generateJwtToken(username);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    /**
     * Logout endpoint
     * Expects { "refreshToken": "xxx" }
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken != null && jwtUtils.validateJwtToken(refreshToken)) {
            String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
            refreshTokenStore.remove(username); // Invalidate
        }

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
