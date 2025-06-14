package com.leon.rest_api.controller;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.leon.rest_api.utils.CommonHashMap;


public abstract class RestService {
	
	public CommonHashMap outputObj = new CommonHashMap();
	public CommonHashMap inputObj = new CommonHashMap();
	public void setInput(Map<String, Object> i) throws Exception{
		inputObj.setHmap(i);
	}
	@Transactional
	public CommonHashMap run() throws Exception{
		 executeProcess();
		 return outputObj;
	}
	


	abstract public void executeProcess() throws Exception;
	abstract public Class<?> getDtoClass(String i);

}
