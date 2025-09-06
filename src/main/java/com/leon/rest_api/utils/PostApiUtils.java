package com.leon.rest_api.utils;

import com.leon.rest_api.controller.PostService;

public abstract class PostApiUtils<I> extends PostService {
	
	protected I input;

	@SuppressWarnings("unchecked")
    public void setInput(Object obj) {
        this.input = (I) obj;
    }
	
}
