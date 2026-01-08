package com.leon.rest_api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonPublicController implements CommonPublicControllerInterface {

	private static final Logger log = LoggerFactory.getLogger(CommonPublicController.class);


    public CommonPublicController() {
    }

	public String testEndpoint() {
		log.info("testing");
		return "OK";
	}
}
