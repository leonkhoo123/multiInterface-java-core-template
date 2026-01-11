package com.leon.common.service;

import com.leon.common.dto.TokenTuple;
import com.leon.common.dto.request.LoginRequest;
import com.leon.common.dto.request.RegisterRequest;
import com.leon.common.dto.response.LoginResponse;
import com.leon.common.dto.response.LogoutResponse;
import com.leon.common.dto.response.RegisterResponse;
import com.leon.common.entities.User;
import com.leon.common.exception.UserAlreadyExistsException;
import com.leon.common.exception.UserNotFoundException;
import com.leon.common.repository.RefreshTokenStore;
import com.leon.common.repository.UserStore;
import com.leon.common.security.JwtTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class UserAuthService {

    private static final Logger log = LoggerFactory.getLogger(UserAuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserStore userStore;
    private final RefreshTokenStore refreshTokenStore;

    public UserAuthService(AuthenticationManager authenticationManager,
                           JwtTokenUtils jwtTokenUtils,
                           PasswordEncoder passwordEncoder, UserStore userStore, RefreshTokenStore refreshTokenStore) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
        this.passwordEncoder = passwordEncoder;
        this.userStore = userStore;
        this.refreshTokenStore = refreshTokenStore;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Authenticate user
        // This will throw BadCredentialsException if password fails, which should be handled by GlobalExceptionHandler to return 401
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details from the Authentication object (avoids a second DB query)
            // This assumes your UserDetailsService returns your custom User entity or you can cast the principal
            User user = userStore.findByUsername(request.getUsername())
                    .orElseThrow(()-> new UserNotFoundException("Username Not Found"));

            // Generate tokens
            String accessToken = jwtTokenUtils.generateAccessToken(authentication);
            String refreshToken = jwtTokenUtils.generateRefreshToken(authentication);
            
            // Get User Agent and IP Address
            String userAgent = null;
            String ipAddress = null;
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest httpRequest = attributes.getRequest();
                userAgent = httpRequest.getHeader("User-Agent");
                ipAddress = httpRequest.getHeader("X-Real-IP");
            }

            // Save refresh token
            refreshTokenStore.saveRefreshToken(user, refreshToken, userAgent, ipAddress, request.getDeviceId());

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userStore.save(user);

            log.info("User logged in successfully: {}", user.getUsername());

            return new LoginResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtTokenUtils.getAccessTokenExpiration(),
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRoles(),
                    request.getDeviceId()
            );
        } catch (BadCredentialsException e){
            log.info("Credential error ",e);
            throw new BadCredentialsException("BadCredential");
        }
    }

    @Transactional
    public LogoutResponse logout(TokenTuple tokens) {
        // 1. Blacklist the Access Token
        jwtTokenUtils.blacklistToken(tokens.getAccessToken());

        // 2. Delete the Refresh Token from DB
        String username = jwtTokenUtils.getUsernameFromToken(tokens.getAccessToken());
        User user = userStore.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        // logout only for that deviceId
        refreshTokenStore.revokeRefreshTokenByDeviceId("logout", user.getId(),tokens.getDeviceId());
        SecurityContextHolder.clearContext();
        return new LogoutResponse(username, LocalDateTime.now());
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());

        if (userStore.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }
        if (userStore.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of("ROLE_USER"));

        userStore.save(user);
        log.info("User registered successfully: {}", request.getUsername());

        return new RegisterResponse(user.getUsername(), user.getEmail(), LocalDateTime.now());
    }
}
