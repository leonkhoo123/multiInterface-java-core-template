package com.leon.rest_api.service;

import com.leon.rest_api.dto.TokenTuple;
import com.leon.rest_api.dto.request.LoginRequest;
import com.leon.rest_api.dto.request.RegisterRequest;
import com.leon.rest_api.dto.response.LoginResponse;
import com.leon.rest_api.dto.response.LogoutResponse;
import com.leon.rest_api.dto.response.RegisterResponse;
import com.leon.rest_api.entities.User;
import com.leon.rest_api.exception.UserAlreadyExistsException;
import com.leon.rest_api.exception.UserNotFoundException;
import com.leon.rest_api.repository.RefreshTokenRepository;
import com.leon.rest_api.repository.UserRepository;
import com.leon.rest_api.security.JwtTokenUtils;
import com.leon.rest_api.security.RefreshTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class UserAuthService {

    private static final Logger log = LoggerFactory.getLogger(UserAuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenUtils refreshTokenUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserAuthService(AuthenticationManager authenticationManager,
                           JwtTokenUtils jwtTokenUtils,
                           UserRepository userRepository,
                           RefreshTokenService refreshTokenService,
                           PasswordEncoder passwordEncoder, RefreshTokenUtils refreshTokenUtils, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenUtils = refreshTokenUtils;
        this.refreshTokenRepository = refreshTokenRepository;
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
        String accessToken = jwtTokenUtils.generateAccessToken(authentication);
        String refreshToken = jwtTokenUtils.generateRefreshToken(authentication);

        // Save refresh token
        refreshTokenUtils.saveRefreshToken(user, refreshToken);

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in successfully: {}", user.getUsername());

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtTokenUtils.getAccessTokenExpiration(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
    }

    @Transactional
    public LogoutResponse logout(TokenTuple tokens) {
        // 1. Blacklist the Access Token
        jwtTokenUtils.blacklistToken(tokens.getAccessToken());

        // 2. Delete the Refresh Token from DB
        String username = jwtTokenUtils.getUsernameFromToken(tokens.getAccessToken());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        // logout from everywhere first
        // TODO : add multi device support and only logout from one device
        refreshTokenRepository.revokeAllRefreshToken("logout", user.getId());
        // refreshTokenUtils.logoutUser(user);
        SecurityContextHolder.clearContext();
        return new LogoutResponse(username, LocalDateTime.now());
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of("ROLE_USER"));

        userRepository.save(user);
        log.info("User registered successfully: {}", request.getUsername());

        return new RegisterResponse(user.getUsername(), user.getEmail(), LocalDateTime.now());
    }
}
