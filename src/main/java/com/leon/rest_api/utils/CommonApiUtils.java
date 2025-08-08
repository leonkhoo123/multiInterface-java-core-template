package com.leon.rest_api.utils;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.leon.rest_api.controller.RestService;

@Component
public abstract class CommonApiUtils<I> extends RestService{
	
	protected I input;

	@SuppressWarnings("unchecked")
    public void setInput(Object obj) {
        this.input = (I) obj;
    }
	
}
