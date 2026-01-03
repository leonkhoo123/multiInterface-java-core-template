package com.leon.rest_api.exception;

import com.leon.rest_api.dto.response.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String TRACE_HEADER = "X-Request-ID";

    private String traceId(HttpServletRequest request) {
        return request.getHeader(TRACE_HEADER);
    }

    // ---------------- Business ----------------

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CommonResponse<Void>> handleUserNotFound(
            UserNotFoundException e,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(CommonResponse.failure(
                        e.getMessage(),
                        ErrorCode.USER_NOT_FOUND.name()
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.failure(
                        e.getMessage(),
                        ErrorCode.INTERNAL_ERROR.name()
                ));
    }

    // ---------------- Validation ----------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.failure(
                        message,
                        ErrorCode.VALIDATION_ERROR.name()
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Void>> handleConstraintViolation(
            ConstraintViolationException e,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.failure(
                        e.getMessage(),
                        ErrorCode.VALIDATION_ERROR.name()
                ));
    }

    // ---------------- Security ----------------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Void>> handleAccessDenied(
            AccessDeniedException e,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponse.failure(
                        "Unauthorized",
                        ErrorCode.UNAUTHORIZED.name()
                ));
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonResponse<Void>> handleAuthenticationException(
            Exception e, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponse.failure(
                        "Unauthorized",
                        ErrorCode.UNAUTHORIZED.name()
                ));
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<CommonResponse<Void>> handleRefreshTokenException(
            RefreshTokenException e,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(CommonResponse.failure(
                        e.getMessage(),
                        ErrorCode.REFRESH_TOKEN_ERROR.name()
                ));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CommonResponse<Void>> handleExpiredJwtException(
            Exception e, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponse.failure(
                        "Barrier Token Expired",
                        ErrorCode.UNAUTHORIZED.name()
                ));
    }


    // ---------------- Fallback ----------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleAll(
            Exception e,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception!", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.failure(
                        "Internal server error",
                        ErrorCode.INTERNAL_ERROR.name()
                ));
    }
}
