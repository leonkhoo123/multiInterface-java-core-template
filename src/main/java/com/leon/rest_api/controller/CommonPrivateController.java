package com.leon.rest_api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/private")
public class CommonPrivateController {
	private static final Logger logger = LoggerFactory.getLogger(CommonPrivateController.class);

	@GetMapping("/test")
	public ResponseEntity<?> postHandler(){
		return ResponseEntity.ok("auth OK");
	}

}




