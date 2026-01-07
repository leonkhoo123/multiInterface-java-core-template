package com.leon.rest_api.controller;

import com.leon.common.dto.response.CommonResponse;
import com.leon.rest_api.dto.response.NovelResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class NovelAuthController implements NovelAuthControllerInterface {
    @Override
    public ResponseEntity<CommonResponse<List<NovelResponse>>> getNovelList() {
        NovelResponse novA = new NovelResponse();
        novA.setName("NovelA");
        novA.setReadPercentage(BigDecimal.valueOf(20));
        NovelResponse novB = new NovelResponse();
        novB.setName("NovelB");
        novB.setReadPercentage(BigDecimal.valueOf(30));
        List<NovelResponse> res = new ArrayList<>();
        res.add(novA);
        res.add(novB);
        return ResponseEntity.ok(CommonResponse.success("Novel_List",res));
    }
}
