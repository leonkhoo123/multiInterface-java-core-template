package com.leon.rest_api.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

import com.leon.rest_api.utils.CommonHashMap;

@Scope("prototype")
public abstract class RestService{

	public CommonHashMap outputObj = new CommonHashMap();

	@Transactional
	public CommonHashMap run() throws Exception {
		executeProcess();
		return outputObj;
	}

	abstract public void setInput(Object obj) throws Exception;
	abstract public void executeProcess() throws Exception;
	abstract public Class<?> getDtoClass(String i);

}
