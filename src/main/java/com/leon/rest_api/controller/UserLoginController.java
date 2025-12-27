package com.leon.rest_api.controller;


import com.leon.rest_api.dto.request.LoginRequest;
import com.leon.rest_api.dto.response.CommonResponse;
import com.leon.rest_api.dto.response.LoginResponse;
import com.leon.rest_api.dto.response.LogoutResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserLoginController implements UserLoginControllerInterface{

    @Override
    public ResponseEntity<CommonResponse<LoginResponse>> login(LoginRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<LogoutResponse>> logout(String authorization) {
        return null;
    }
}
