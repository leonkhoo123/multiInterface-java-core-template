package com.leon.rest_api.controller;

import com.leon.rest_api.dto.request.LoginRequest;
import com.leon.rest_api.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/")
@Tag(name = "Login Api", description = "User login management APIs")
public interface UserAuthControllerInterface {

    // -------------------- LOGIN --------------------

    @Operation(
            summary = "User login",
            description = "Authenticate user and return access token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            schema = @Schema(allOf = {CommonResponse.class, LoginResponse.class})

                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    @PostMapping("/login")
    ResponseEntity<CommonResponse<LoginResponse>> login(
            LoginRequest request
    );

    // -------------------- LOGOUT --------------------

    @Operation(
            summary = "User logout",
            description = "Invalidate current access token"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful",
                    content = @Content(
                            schema = @Schema(allOf = {CommonResponse.class, LogoutResponse.class})
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    @PostMapping("/logout")
    ResponseEntity<CommonResponse<LogoutResponse>> logout(
            HttpServletRequest request
    );
}
