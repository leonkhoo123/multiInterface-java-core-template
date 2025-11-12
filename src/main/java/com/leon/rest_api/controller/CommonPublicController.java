package com.leon.rest_api.controller;

import com.leon.rest_api.dto.UserInfoInquiryDTOInput;
import com.leon.rest_api.dto.UserInfoInquiryDTOOutput;
import com.leon.rest_api.service.UserInfoTest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "General usage", description = "General Public APIs")
public class CommonPublicController {

//	private static final Logger log = LoggerFactory.getLogger(CommonPublicController.class);
	private final UserInfoTest userInfoTest;

    public CommonPublicController(UserInfoTest userInfoTest) {
        this.userInfoTest = userInfoTest;
    }

    @GetMapping("/test")
	@Operation(summary = "Health check")
	public String testEndpoint() {
		return "OK";
	}

	@PostMapping("/users/info")
	@Operation(summary = "Get user info by body")
	public ResponseEntity<UserInfoInquiryDTOOutput> getUserInfo(@Valid @RequestBody UserInfoInquiryDTOInput input) throws Exception {
		return ResponseEntity.ok(userInfoTest.executeProcess(input));
	}

	@GetMapping("/users/{userId}")
	@Operation(summary = "Get user info by path variable")
	public ResponseEntity<UserInfoInquiryDTOOutput> getUserById(@PathVariable BigDecimal userId) throws Exception {
		UserInfoInquiryDTOInput input = new UserInfoInquiryDTOInput();
		input.USERID = userId;
		return ResponseEntity.ok(userInfoTest.executeProcess(input));
	}
}
