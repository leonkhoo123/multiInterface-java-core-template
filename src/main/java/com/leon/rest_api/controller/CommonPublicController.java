package com.leon.rest_api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonPublicController implements CommonPublicControllerInterface {

	private static final Logger log = LoggerFactory.getLogger(CommonPublicController.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

    public CommonPublicController() {
    }

	public String testEndpoint() {
		for(int i = 0; i<100;i++){
			String msg = "{\"processName\":\"userInfoInquiry\",\"input\":{\"USERID\":"+i+"}}";
			kafkaTemplate.send("api-service", msg);
		}
		log.info("testing");
		return "OK";
	}
}
