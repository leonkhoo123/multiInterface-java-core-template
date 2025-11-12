package com.leon.rest_api.service;

import com.leon.rest_api.Exception.UserNotFoundException;
import com.leon.rest_api.dto.UserInfoInquiryDTOInput;
import com.leon.rest_api.dto.UserInfoInquiryDTOOutput;
import com.leon.rest_api.entities.UserInfo;
import com.leon.rest_api.repository.UserInfoRepository;
import com.leon.rest_api.utils.DtoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInfoTest {

	private static final Logger logger = LoggerFactory.getLogger(UserInfoTest.class);
	private final UserInfoRepository userInfoRepository;

	// Constructor injection
	public UserInfoTest(UserInfoRepository userInfoRepository) {
		this.userInfoRepository = userInfoRepository;
	}

	@Transactional(readOnly = true)
	public UserInfoInquiryDTOOutput executeProcess(UserInfoInquiryDTOInput input) throws Exception {
//		input = DtoUtils.cleanDto(input); // assign default value to all column

		logger.info("Processing user info inquiry for userId: {}", input.USERID);

		UserInfo userInfo = userInfoRepository.findByUserId(input.USERID)
				.orElseThrow(() -> {
					logger.warn("User not found with userId: {}", input.USERID);
					return new UserNotFoundException("User not found: " + input.USERID);
				});

		UserInfoInquiryDTOOutput output = new UserInfoInquiryDTOOutput();
		output.USERID = userInfo.getUserId();
		output.BALANCE = userInfo.getBalance();
		output.USERNAME = userInfo.getUsername();

		logger.info("Successfully retrieved user info for userId: {}", input.USERID);
//		return DtoUtils.cleanDto(output);
		return output;
	}
}