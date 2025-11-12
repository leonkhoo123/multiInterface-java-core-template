package com.leon.rest_api.service;

import com.leon.rest_api.controller.GetService;
import com.leon.rest_api.dto.UserInfoInquiryDTOInput;
import com.leon.rest_api.dto.UserInfoInquiryDTOOutput;
import com.leon.rest_api.entities.UserInfo;
import com.leon.rest_api.repository.UserInfoRepository;
import com.leon.rest_api.utils.CommonHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service("userInfoInquiryGetter")
public class UserInfoInquiryGetter extends GetService<UserInfoInquiryDTOInput> {
	private static final Logger logger = LoggerFactory.getLogger(UserInfoInquiryGetter.class);

	private final UserInfoRepository userInfoRepository;

	public UserInfoInquiryGetter(UserInfoRepository userInfoRepository) {
		this.userInfoRepository = userInfoRepository;
	}

	@Override
	public Class<?> getDtoClass(Enum t) {
	    if (t == dto.INPUT) {
	        return UserInfoInquiryDTOInput.class;
	    } else {
	        return UserInfoInquiryDTOOutput.class;
	    }
	}

	@Override
	public CommonHashMap executeProcess(UserInfoInquiryDTOInput objInput) throws Exception {
		//singleton call, so initialize variable here
		UserInfoInquiryDTOInput input = (UserInfoInquiryDTOInput) objInput;  // direct cast
		CommonHashMap outputObj = new CommonHashMap();

		// query something
//		logger.info("Do something with USERID : "+input.USERID);
		Optional<UserInfo> result = userInfoRepository.findByUserId(input.USERID);
		if (!result.isPresent()) {
			logger.info("Data Not Found");
			throw new Exception("Data Not Found");
		}
		UserInfo temp = result.get();
		outputObj.setHmap(temp);
//		logger.info("Api Done output: "+outputObj);
		return outputObj;
	}
}
