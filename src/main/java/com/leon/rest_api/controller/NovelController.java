package com.leon.rest_api.controller;

import com.leon.common.dto.response.CommonResponse;
import com.leon.rest_api.processor.NovelProcessor;
import com.leon.rest_api.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/novel")
@Tag(name = "Novel Public Endpoint", description = "Novel App related public endpoint")
public class NovelController {
    private final AiService aiService;
    private final NovelProcessor novelProcessor;

    public NovelController(AiService aiService, NovelProcessor novelProcessor) {
        this.aiService = aiService;
        this.novelProcessor = novelProcessor;
    }

    @GetMapping("/testAi")
    public ResponseEntity<CommonResponse<String>> testAi() {
        String response = aiService.inquiryLocalLlm("Testing on my connection, response {\"message\":\"唉，我在\"}");
        return ResponseEntity.ok(CommonResponse.success("Inquiry Response", response));
    }

    @GetMapping("/startNovelProcessor")
    public ResponseEntity<CommonResponse<String>> startNovelProcessor() {
        novelProcessor.consumeNovel();
        return ResponseEntity.ok(CommonResponse.success("Inquiry Response", "oh yeah"));
    }

}
