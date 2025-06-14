package com.leon.rest_api.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.leon.rest_api.controller.RestService;
import com.leon.rest_api.entities.UserInfo;
import com.leon.rest_api.repository.UserInfoRepository;
import com.leon.rest_api.dto.UserInfoDTOInput;
import com.leon.rest_api.dto.UserInfoDTOOutput;

@Service("userInfo")
public class A16001_UserInfo extends RestService {

	public A16001_UserInfo(UserInfoRepository userInfoRepository) {
		this.userInfoRepository = userInfoRepository;
	}
	
	@Override
	public Class<?> getDtoClass(String t) {
	    if (t.equalsIgnoreCase("i")) {
	        return UserInfoDTOInput.class;
	    } else {
	        return UserInfoDTOOutput.class;
	    }
	}

	private UserInfoRepository userInfoRepository;

	public void executeProcess() throws Exception{
		// query something
		Optional<UserInfo> result = userInfoRepository.findByUserId(inputObj.getBigDecimal("USERID"));
		if (!result.isPresent()) {
			throw new Exception("Data Not Found");
		}
		UserInfo temp = new UserInfo();
		temp = result.get();
		outputObj.setHmap(temp);
	}

}
