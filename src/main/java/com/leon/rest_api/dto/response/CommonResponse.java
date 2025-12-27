package com.leon.rest_api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    public boolean success;
    public String message;
    public String errorCode;
    public T data;

    public static <T> CommonResponse<T> success(String message, T data) {
        CommonResponse<T> res = new CommonResponse<>();
        res.success = true;
        res.message = message;
        res.data = data;
        return res;
    }

    public static <T> CommonResponse<T> failure(String message, String errorCode) {
        CommonResponse<T> res = new CommonResponse<>();
        res.success = false;
        res.message = message;
        res.errorCode = errorCode;
        res.data = null;
        return res;
    }
}

