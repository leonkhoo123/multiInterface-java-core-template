//package com.leon.rest_api.service;
//
//import com.leon.rest_api.PrototypeService;
//import com.leon.rest_api.dto.UserInfoInquiryDTOInput;
//import com.leon.rest_api.dto.UserInfoInquiryDTOOutput;
//import com.leon.rest_api.service.ob.UserInfoInquiryOB;
//import com.leon.rest_api.service.ob.UserInfoInquiryRepos;
//import com.leon.rest_api.utils.PostApiUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
////@Service("userInfoInquiry")
//@PrototypeService("userInfoInquiry")
//public class UserInfoInquiry extends PostApiUtils<UserInfoInquiryDTOInput> {
//	private static final Logger logger = LoggerFactory.getLogger(UserInfoInquiry.class);
//
//	private final UserInfoInquiryRepos repo;
//	private UserInfoInquiryOB OB;
//
//	public UserInfoInquiry(UserInfoInquiryRepos repo) {
//		this.repo = repo;
//		OB = repo.createOB();
//	}
//
//	@Override
//	public Class<?> getDtoClass(Enum t) {
//	    if (t == dto.INPUT) {
//	        return UserInfoInquiryDTOInput.class;
//	    } else {
//	        return UserInfoInquiryDTOOutput.class;
//	    }
//	}
//
//	public void executeProcess() throws Exception{
//		// query something
////        logger.info("Do something with USERID :{}", input.USERID);
//		outputObj.setHmap(OB.getUserInfo(input.USERID));
////        logger.info("Api Done output: {}", outputObj);
//	}
//}
