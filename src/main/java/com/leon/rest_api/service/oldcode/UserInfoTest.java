//package com.leon.rest_api.service;
//
//import com.leon.common.exception.UserNotFoundException;
//import com.leon.rest_api.dto.request.UserInfoInquiryRequest;
//import com.leon.rest_api.dto.response.UserInfoInquiryResponse;
//import com.leon.rest_api.entities.UserInfo;
//import com.leon.rest_api.repository.UserInfoRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class UserInfoTest {
//
//	private static final Logger logger = LoggerFactory.getLogger(UserInfoTest.class);
//	private final UserInfoRepository userInfoRepository;
//
//	// Constructor injection
//	public UserInfoTest(UserInfoRepository userInfoRepository) {
//		this.userInfoRepository = userInfoRepository;
//	}
//
//	@Transactional(readOnly = true)
//	public UserInfoInquiryResponse executeProcess(UserInfoInquiryRequest input) throws Exception {
////		input = DtoUtils.cleanDto(input); // assign default value to all column
//
//		logger.info("Processing user info inquiry for userId: {}", input.getUserid());
//
//		UserInfo userInfo = userInfoRepository.findByUserId(input.getUserid())
//				.orElseThrow(() -> {
//					logger.warn("User not found with userId: {}", input.getUserid());
//					return new UserNotFoundException("User not found: " + input.getUserid());
//				});
//
//		UserInfoInquiryResponse output = new UserInfoInquiryResponse();
//		output.setUserid(userInfo.getUserId());
//		output.setBalance(userInfo.getBalance());
//		output.setUsername(userInfo.getUsername());
//
//		logger.info("Successfully retrieved user info for userId: {}", input.getUserid());
////		return DtoUtils.cleanDto(output);
//		return output;
//	}
//}