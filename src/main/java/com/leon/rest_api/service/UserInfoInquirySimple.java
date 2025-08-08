package com.leon.rest_api.service;

import com.leon.rest_api.dto.UserInfoInquiryDTOInput;
import com.leon.rest_api.dto.UserInfoInquiryDTOOutput;
import com.leon.rest_api.entities.UserInfo;
import com.leon.rest_api.repository.UserInfoRepository;
import com.leon.rest_api.utils.CommonApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service("userInfoInquirySimple")
public class UserInfoInquirySimple extends CommonApiUtils<UserInfoInquiryDTOInput> {
	private static final Logger logger = LoggerFactory.getLogger(UserInfoInquirySimple.class);

	private UserInfoRepository userInfoRepository;

	public UserInfoInquirySimple(UserInfoRepository userInfoRepository) {
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
			logger.info("Data Not Found");
			throw new Exception("Data Not Found");
		}
		UserInfo temp = new UserInfo();
		temp = result.get();
		outputObj.setHmap(temp);
		logger.info("Api Done output: "+outputObj);

	}

}
