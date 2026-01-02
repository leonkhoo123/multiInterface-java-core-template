package com.leon.rest_api.service;

import com.leon.rest_api.config.JwtProperties;
import com.leon.rest_api.dto.TokenTuple;
import com.leon.rest_api.dto.request.LoginRequest;
import com.leon.rest_api.dto.response.LoginResponse;
import com.leon.rest_api.dto.response.LogoutResponse;
import com.leon.rest_api.entities.RefreshToken;
import com.leon.rest_api.entities.User;
import com.leon.rest_api.exception.RefreshTokenException;
import com.leon.rest_api.exception.UserNotFoundException;
import com.leon.rest_api.repository.RefreshTokenRepository;
import com.leon.rest_api.repository.UserRepository;
import com.leon.rest_api.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       JwtProperties jwtProperties) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Authenticate user
        // This will throw BadCredentialsException if password fails, which should be handled by GlobalExceptionHandler to return 401
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details from the Authentication object (avoids a second DB query)
        // This assumes your UserDetailsService returns your custom User entity or you can cast the principal
        User user = (User) authentication.getPrincipal();

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Save refresh token
        saveRefreshToken(user.getId(), refreshToken);

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in successfully: {}", user.getUsername());

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
    }

    @Transactional
    public LogoutResponse logout(String accessToken) {
        // 1. Blacklist the Access Token
        jwtTokenProvider.blacklistToken(accessToken);

        // 2. Delete the Refresh Token from DB
        String username = jwtTokenProvider.getUsernameFromToken(accessToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        deleteByUserId(user.getId());

        SecurityContextHolder.clearContext();
        return new LogoutResponse(username, LocalDateTime.now());
    }

    @Transactional
    public TokenTuple rotateRefreshToken(String refreshToken) {
        // 1. Find the token in DB
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found"));

        // 2. Verify expiration
        verifyExpiration(refreshTokenEntity);

        // 3. Get the user
        User user = refreshTokenEntity.getUser();

        // 4. Generate NEW access token
        // We create a temporary Authentication object for the provider
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);

        // 5. Generate NEW refresh token (ROTATION)
        // This method should delete the old one and save the new one
        RefreshToken newRefreshTokenEntity = createRefreshToken(user.getId());
        return new TokenTuple(accessToken, newRefreshTokenEntity.getToken());
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshToken().getExpiration()));
        refreshToken.setToken(UUID.randomUUID().toString()); // Simple UUID for database token

        // Delete old token if exists (One token per user)
        refreshTokenRepository.deleteByUser(refreshToken.getUser());

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if user already has a token and delete it (Rotation/Single Session)
        // If you want to allow multiple devices, remove this delete line
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setToken(refreshToken);

        // Use 7 days from now (matching your JwtTokenProvider logic)
        refreshTokenEntity.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshToken().getExpiration()));

        refreshTokenRepository.save(refreshTokenEntity);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenException("Refresh Token Expired");
        }
        return refreshToken;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return refreshTokenRepository.deleteByUser(user);
    }
}
