package com.leon.rest_api.controller;

import com.leon.common.dto.response.CommonResponse;
import com.leon.common.security.JwtTokenUtils;
import com.leon.rest_api.dto.request.NovelContentRequest;
import com.leon.rest_api.dto.request.UpdateUserNovelProgressRequest;
import com.leon.rest_api.dto.response.GetNovelListResponse;
import com.leon.rest_api.dto.response.GetUserNovelProgressResponse;
import com.leon.rest_api.dto.response.NovelContentResponse;
import com.leon.rest_api.dto.response.NovelResponse;
import com.leon.rest_api.service.NovelService;
import com.leon.rest_api.service.NovelUserProgressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/v1/private/novel")
@Tag(name = "Novel Endpoint", description = "Novel App related auth endpoint")
@RestController
public class NovelAuthController {
    private static final Logger log = LoggerFactory.getLogger(NovelAuthController.class);

    private final NovelService novelService;
    private final JwtTokenUtils jwtTokenUtils;
    private final NovelUserProgressService novelUserProgressService;

    public NovelAuthController(NovelService novelService, JwtTokenUtils jwtTokenUtils, NovelUserProgressService novelUserProgressService) {
        this.novelService = novelService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.novelUserProgressService = novelUserProgressService;
    }

    @PostMapping("/novelContent")
    public ResponseEntity<CommonResponse<NovelContentResponse>> novelContent(
            HttpServletRequest request, @Valid @RequestBody NovelContentRequest body) {
        String accessToken = jwtTokenUtils.getJwtFromRequest(request);
        String username = jwtTokenUtils.getUsernameFromToken(accessToken);
        NovelContentResponse response = novelService.getNovelContent(username, body);
        return ResponseEntity.ok(CommonResponse.success("", response));
    }

    @GetMapping("/getNovelList")
    public ResponseEntity<CommonResponse<GetNovelListResponse>> getNovelList(HttpServletRequest request) {
        String accessToken = jwtTokenUtils.getJwtFromRequest(request);
        String username = jwtTokenUtils.getUsernameFromToken(accessToken);
        GetNovelListResponse response = novelService.getNovelList(username);
        return ResponseEntity.ok(CommonResponse.success("", response));
    }

    @PostMapping("/updateUserNovelProgress")
    public ResponseEntity<CommonResponse<String>> updateUserNovelProgress(
            HttpServletRequest request, @Valid @RequestBody UpdateUserNovelProgressRequest body) {
        String accessToken = jwtTokenUtils.getJwtFromRequest(request);
        String username = jwtTokenUtils.getUsernameFromToken(accessToken);
        novelUserProgressService.updateUserNovelProgress(username,body);
        return ResponseEntity.ok(CommonResponse.success("", "Successfully updated user progress"));
    }

    @GetMapping("/getUserNovelProgress/{novelId}")
    public ResponseEntity<CommonResponse<GetUserNovelProgressResponse>> getUserNovelProgress(
            HttpServletRequest request, @PathVariable @NotNull(message="Novel id cannot be null") Long novelId) {
        String accessToken = jwtTokenUtils.getJwtFromRequest(request);
        String username = jwtTokenUtils.getUsernameFromToken(accessToken);
        GetUserNovelProgressResponse response = novelUserProgressService.getUserNovelProgress(username, novelId);
        return ResponseEntity.ok(CommonResponse.success("", response));
    }
}
