package com.leon.common.exception;

import com.leon.common.dto.response.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public interface GlobalExceptionHandlerInterface {

    // ---------------- Business ----------------

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CommonResponse<Void>> handleUserNotFound(
            UserNotFoundException e,
            HttpServletRequest request
    );

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    );

    // ---------------- Validation ----------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    );

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Void>> handleConstraintViolation(
            ConstraintViolationException e,
            HttpServletRequest request
    );

    // ---------------- Security ----------------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Void>> handleAccessDenied(
            AccessDeniedException e,
            HttpServletRequest request
    );

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonResponse<Void>> handleAuthenticationException(
            Exception e, HttpServletRequest request);

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<CommonResponse<Void>> handleRefreshTokenException(
            RefreshTokenException e,
            HttpServletRequest request
    );

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CommonResponse<Void>> handleExpiredJwtException(
            Exception e, HttpServletRequest request);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CommonResponse<Void>> handleBadCredentialsException(
            Exception e, HttpServletRequest request);

    // ---------------- Fallback ----------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleAll(
            Exception e,
            HttpServletRequest request
    );
}
