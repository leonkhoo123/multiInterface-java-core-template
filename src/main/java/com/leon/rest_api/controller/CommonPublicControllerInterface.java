package com.leon.rest_api.controller;

import com.leon.rest_api.dto.request.UserInfoInquiryRequest;
import com.leon.rest_api.dto.response.UserInfoInquiryResponse;
import com.leon.common.dto.response.CommonResponse;
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

@RequestMapping("/api/v1/")
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
}
