//package com.leon.rest_api.controller;
//
//import com.leon.common.utils.CommonHashMap;
//import org.springframework.transaction.annotation.Transactional;
//
//public abstract class PostService {
//
//	public CommonHashMap outputObj = new CommonHashMap();
//	public enum dto {
//		INPUT,
//		OUTPUT
//	}
//
//	@Transactional
//	public CommonHashMap run() throws Exception {
//		executeProcess();
//		return outputObj;
//	}
//
//	abstract public void setInput(Object obj) throws Exception;
//	abstract public void executeProcess() throws Exception;
//	abstract public Class<?> getDtoClass(Enum i);
//
//}
