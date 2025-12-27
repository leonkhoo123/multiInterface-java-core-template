package com.leon.rest_api.controller;

import com.leon.rest_api.dto.request.UserInfoInquiryRequest;
import com.leon.rest_api.dto.response.UserInfoInquiryResponse;
import com.leon.rest_api.dto.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RequestMapping("/api/v1")
@Tag(name = "General usage", description = "General Public APIs")
public interface CommonPublicControllerInterface {

    // ------------------- HEALTH CHECK -------------------

    @GetMapping("/test")
    @Operation(summary = "Health check")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service is healthy",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    String testEndpoint();

    // ------------------- POST USER INFO -------------------

    @PostMapping("/users/info")
    @Operation(summary = "Get user info by body")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User info retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserInfoInquiryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    ResponseEntity<CommonResponse<UserInfoInquiryResponse>> postUserInfo(@Valid @RequestBody UserInfoInquiryRequest input) throws Exception;

    // ------------------- GET USER INFO BY ID -------------------

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user info by path variable")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User info retrieved successfully",
                    content = @Content(schema = @Schema(
                            allOf = {CommonResponse.class, UserInfoInquiryResponse.class}))),
            @ApiResponse(responseCode = "400", description = "Invalid user ID",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    ResponseEntity<CommonResponse<UserInfoInquiryResponse>> getUserById(@PathVariable BigDecimal userId) throws Exception;
}
