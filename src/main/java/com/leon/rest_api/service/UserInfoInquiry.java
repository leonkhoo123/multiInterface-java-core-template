package com.leon.rest_api.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.leon.rest_api.dto.UserInfoInquiryDTOInput;
import com.leon.rest_api.dto.UserInfoInquiryDTOOutput;
import com.leon.rest_api.entities.UserInfo;
import com.leon.rest_api.repository.UserInfoRepository;
import com.leon.rest_api.utils.CommonApiUtils;


@Service("userInfoInquiry")
public class UserInfoInquiry extends CommonApiUtils<UserInfoInquiryDTOInput> {

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
		Optional<UserInfo> result = userInfoRepository.findByUserId(input.USERID);
		if (!result.isPresent()) {
			throw new Exception("Data Not Found");
		}
		UserInfo temp = new UserInfo();
		temp = result.get();
		outputObj.setHmap(temp);
	}

}
