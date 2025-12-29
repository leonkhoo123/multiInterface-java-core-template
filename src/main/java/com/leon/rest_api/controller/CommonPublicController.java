package com.leon.rest_api.controller;

import com.leon.rest_api.dto.request.UserInfoInquiryRequest;
import com.leon.rest_api.dto.response.UserInfoInquiryResponse;
import com.leon.rest_api.dto.response.CommonResponse;
import com.leon.rest_api.service.UserInfoTest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class CommonPublicController implements CommonPublicControllerInterface {

//	private static final Logger log = LoggerFactory.getLogger(CommonPublicController.class);
	private final UserInfoTest userInfoTest;

    public CommonPublicController(UserInfoTest userInfoTest) {
        this.userInfoTest = userInfoTest;
    }

	public String testEndpoint() {
		return "OK";
	}

	@Override
	public ResponseEntity<CommonResponse<UserInfoInquiryResponse>> postUserInfo(@Valid @RequestBody UserInfoInquiryRequest input) throws Exception {
		UserInfoInquiryResponse output = userInfoTest.executeProcess(input);
		return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("",output));
	}

	@Override
	public ResponseEntity<CommonResponse<UserInfoInquiryResponse>> getUserById(@PathVariable BigDecimal userId) throws Exception {
		UserInfoInquiryRequest input = new UserInfoInquiryRequest(userId);
		UserInfoInquiryResponse output = userInfoTest.executeProcess(input);
		return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("",output));
	}
}
