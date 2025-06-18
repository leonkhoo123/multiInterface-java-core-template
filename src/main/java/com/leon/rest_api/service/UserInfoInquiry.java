package com.leon.rest_api.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.leon.rest_api.dto.UserInfoInquiryDTOInput;
import com.leon.rest_api.dto.UserInfoInquiryDTOOutput;
import com.leon.rest_api.entities.UserInfo;
import com.leon.rest_api.repository.UserInfoRepository;
import com.leon.rest_api.utils.CommonApiUtils;


@Service("userInfoInquiry")
public class UserInfoInquiry extends CommonApiUtils<UserInfoInquiryDTOInput> {
	private static final Logger logger = LoggerFactory.getLogger(UserInfoInquiry.class);

	private UserInfoRepository userInfoRepository;

	public UserInfoInquiry(UserInfoRepository userInfoRepository) {
		this.userInfoRepository = userInfoRepository;
	}
	
	@Override
	public Class<?> getDtoClass(String t) {
	    if (t.equalsIgnoreCase("i")) {
	        return UserInfoInquiryDTOInput.class;
	    } else {
	        return UserInfoInquiryDTOOutput.class;
	    }
	}

	public void executeProcess() throws Exception{
		// query something
		logger.info("Do something with USERID :"+input.USERID);
		Optional<UserInfo> result = userInfoRepository.findByUserId(input.USERID);
		if (!result.isPresent()) {
			throw new Exception("Data Not Found");
//			logger.info("Data Not Found");
		}
		UserInfo temp = new UserInfo();
		temp = result.get();
		outputObj.setHmap(temp);
		logger.info("Api Done output: "+outputObj);

	}

}
