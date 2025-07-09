package com.leon.rest_api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonApiController {
	private static final Logger logger = LoggerFactory.getLogger(CommonApiController.class);

	@Autowired
	private ControllerService controllerService;

	@PostMapping("/{processName}")
	public Object apiHandler(@PathVariable String processName, @RequestBody String input) {
		long startTime = System.currentTimeMillis();  // start timer
		logger.info("Received: {} request, with input: {}", processName, input);

		Object result = controllerService.serve(processName, input);

		long endTime = System.currentTimeMillis();    // end timer
		long elapsed = endTime - startTime;
		logger.info("Process [{}] completed, Elapsed: [{}ms]", processName, elapsed);
		return result;
	}

	@KafkaListener(topics = "api-service", groupId = "api-service-group")
	public void kafkaConsumer(String message) {
		try {
			long startTime = System.currentTimeMillis();  // start timer
			logger.info("Received from Topic: [api-service] Kafka message: {}", message);
			ObjectMapper objectMapper = new ObjectMapper(); // add this line
			JsonNode node = objectMapper.readTree(message);
			String processName = node.get("processName").asText();
			String input = node.get("input").toString(); // raw JSON string

			Object result = controllerService.serve(processName, input);

			long endTime = System.currentTimeMillis();    // end timer
			long elapsed = endTime - startTime;
			logger.info("Kafka processing for ProcessName: [{}] completed, Elapsed: [{}ms]", processName, elapsed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


