package com.leon.rest_api.controller;

import com.leon.common.dto.response.CommonResponse;
import com.leon.rest_api.dto.request.NovelContentRequest;
import com.leon.rest_api.dto.response.GetNovelListResponse;
import com.leon.rest_api.dto.response.NovelContentResponse;
import com.leon.rest_api.service.NovelProcessorService;
import com.leon.rest_api.service.AiService;
import com.leon.rest_api.service.NovelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/novel")
@Tag(name = "Novel Public Endpoint", description = "Novel App related public endpoint")
public class NovelController {
    private final AiService aiService;
    private final NovelProcessorService novelProcessorService;
    private final NovelService novelService;

    public NovelController(AiService aiService, NovelProcessorService novelProcessorService, NovelService novelService) {
        this.aiService = aiService;
        this.novelProcessorService = novelProcessorService;
        this.novelService = novelService;
    }

    @GetMapping("/testAi")
    public ResponseEntity<CommonResponse<String>> testAi() {
        String response = aiService.inquiryLocalLlm("Testing on my connection, response {\"message\":\"唉，我在\"}");
        return ResponseEntity.ok(CommonResponse.success("Inquiry Response", response));
    }

    @GetMapping("/startNovelProcessor")
    public ResponseEntity<CommonResponse<String>> startNovelProcessor() {
        novelProcessorService.consumeNovel();
        return ResponseEntity.ok(CommonResponse.success("Inquiry Response", "oh yeah"));
    }

}
