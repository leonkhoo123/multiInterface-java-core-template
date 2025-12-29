package com.leon.rest_api.service;

import com.leon.rest_api.dto.TokenTuple;
import com.leon.rest_api.dto.request.LoginRequest;
import com.leon.rest_api.dto.response.LoginResponse;
import com.leon.rest_api.dto.response.LogoutResponse;
import com.leon.rest_api.entities.RefreshToken;
import com.leon.rest_api.entities.User;
import com.leon.rest_api.repository.UserRepository;
import com.leon.rest_api.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository,
                       RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Generate tokens
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            // Save refresh token
            refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            log.info("User logged in successfully: {}", user.getUsername());

            // 3. Replaced Builder with a standard Constructor
            return new LoginResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtTokenProvider.getAccessTokenExpiration(),
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRoles()
            );

        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    @Transactional
    public LogoutResponse logout(String accessToken) {
        // 1. Blacklist the Access Token
        jwtTokenProvider.blacklistToken(accessToken);

        // 2. Delete the Refresh Token from DB
        String username = jwtTokenProvider.getUsernameFromToken(accessToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenService.deleteByUserId(user.getId());

        SecurityContextHolder.clearContext();
        return new LogoutResponse(username, LocalDateTime.now());
    }

    @Transactional
    public TokenTuple rotateRefreshToken(String oldRefreshTokenValue) {
        // 1. Find the token in DB
        RefreshToken refreshToken = refreshTokenService.findByToken(oldRefreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        // 2. Verify expiration
        refreshTokenService.verifyExpiration(refreshToken);

        // 3. Get the user
        User user = refreshToken.getUser();

        // 4. Generate NEW access token
        // We create a temporary Authentication object for the provider
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);

        // 5. Generate NEW refresh token (ROTATION)
        // This method should delete the old one and save the new one
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new TokenTuple(newAccessToken, newRefreshToken.getToken());
    }
}