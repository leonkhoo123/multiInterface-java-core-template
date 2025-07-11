package com.leon.rest_api.service;

import com.leon.rest_api.controller.RestService;
import com.leon.rest_api.dto.UserInfoInquiryDTOInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service("kafkaProducer")
public class KafkaProducer extends RestService {
	private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public KafkaProducer() {
	}

	public void executeProcess() throws Exception{
		for(int i = 0; i<100;i++){
			String msg = "{\"processName\":\"userInfoInquiry\",\"input\":{\"USERID\":"+i+"}}";
			kafkaTemplate.send("api-service", msg);
		}
	}

	@Override
	public void setInput(Object obj) throws Exception {
	}

	@Override
	public Class<?> getDtoClass(String i) {
		return UserInfoInquiryDTOInput.class;
	}

}
