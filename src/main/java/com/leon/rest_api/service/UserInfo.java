package com.leon.rest_api.service;

import org.springframework.stereotype.Service;

import com.leon.rest_api.controller.RestService;


@Service("usersInfo")
public class UserInfo extends RestService{
	
	private String var = "ABC";
	
	public String executeProcess() {
		
		process();
		return var;
	}
	
	private void process() {
		//query something
		//set the var
		var = "CBD";
	}
}
