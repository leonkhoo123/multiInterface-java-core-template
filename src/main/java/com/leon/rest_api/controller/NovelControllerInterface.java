package com.leon.rest_api.controller;


import com.leon.common.dto.response.CommonResponse;
import com.leon.rest_api.dto.response.NovelResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/v1/auth/novel")
@Tag(name = "Novel Endpoint", description = "Novel App related endpoint")
public interface NovelControllerInterface {

    // ------------------- GET NOVEL LIST -------------------
     @GetMapping("/novelList")
     @Operation(summary = "Get all novels")
     @ApiResponses({
             @ApiResponse(responseCode = "200", description = "Novel list retrieved successfully",
                     content = @Content(array = @ArraySchema(schema = @Schema(implementation = NovelResponse.class))))
     })
     ResponseEntity<CommonResponse<List<NovelResponse>>> getNovelList();


}
