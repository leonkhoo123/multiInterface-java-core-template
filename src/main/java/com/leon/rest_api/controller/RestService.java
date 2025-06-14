package com.leon.rest_api.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Scope("prototype")
public abstract class RestService {

	@Transactional
	public String serve() {
		return executeProcess();
	}

	abstract public String executeProcess();
}
