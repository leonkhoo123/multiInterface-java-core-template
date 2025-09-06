package com.leon.rest_api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.rest_api.utils.CommonDTOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class CommonApiController {
	private static final Logger logger = LoggerFactory.getLogger(CommonApiController.class);

	@Autowired
	private ControllerService controllerService;
	@Autowired
	private ObjectMapper objectMapper;

	@PostMapping(value = "/{processName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> postHandler(@PathVariable String processName,
										 @RequestBody Map<String, Object> input) {
		long startTime = System.currentTimeMillis();
		logger.info("Received POST: {} request, with input: {}", processName, input);
		Object result = null;
		HttpStatus status = HttpStatus.OK;
		try {
			result = controllerService.postServe(processName, input);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			long elapsed = System.currentTimeMillis() - startTime;
			logger.error("Process [{}] hit exception, Elapsed: [{}ms], with stack trace :",
					processName, elapsed,e);
			result = Map.of("error", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			return ResponseEntity.status(status).body(result);
		} finally {
			long elapsed = System.currentTimeMillis() - startTime;
			logger.info("Process [{}] completed with status [{}], Result: {}, Elapsed: [{}ms]",
					processName, status, CommonDTOUtils.toJson(result), elapsed);
		}
	}

	@GetMapping(value = "/{processName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getHandler(@PathVariable String processName,
										@RequestParam(required = false) Map<String, String> params) {
		long startTime = System.currentTimeMillis(); // start timer
		logger.info("Received GET: {} request, with params: {}", processName, params);
		Object result = null;
		HttpStatus status = HttpStatus.OK;
		try {
			result = controllerService.getServe(processName, params);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			long elapsed = System.currentTimeMillis() - startTime;
			logger.error("Process [{}] hit exception, Elapsed: [{}ms], with stack trace :",
					processName, elapsed,e);
			result = Map.of("error", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			return ResponseEntity.status(status).body(result);
		} finally {
			long elapsed = System.currentTimeMillis() - startTime;
			logger.info("Process [{}] completed with status [{}], Result: {}, Elapsed: [{}ms]",
					processName, status, CommonDTOUtils.toJson(result), elapsed);
		}
	}


	@KafkaListener(topics = "api-service", groupId = "api-service-group")
	public void kafkaConsumer(String message) {
		try {
			long startTime = System.currentTimeMillis();  // start timer
			logger.info("Received from Topic: [api-service] Kafka message: {}", message);

			JsonNode node = objectMapper.readTree(message);
			String processName = node.get("processName").asText();

			// convert "input" node to Map<String, Object>
			Map<String, Object> input = objectMapper.convertValue(
					node.get("input"),
					new TypeReference<Map<String, Object>>() {}
			);
			Object result = controllerService.postServe(processName, input);

			long elapsed = System.currentTimeMillis() - startTime;
			logger.info("Kafka processing for ProcessName: [{}] completed, Elapsed: [{}ms]", processName, elapsed);

		} catch (Exception e) {
			logger.error("Error while processing Kafka message", e);
		}
	}
}


