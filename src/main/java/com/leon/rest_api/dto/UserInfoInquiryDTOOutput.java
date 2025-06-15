package com.leon.rest_api.dto;

import java.math.BigDecimal;

import com.leon.rest_api.utils.DTOUtils;


public class UserInfoInquiryDTOOutput extends DTOUtils{
    public BigDecimal USERID;
    public String USERNAME;
    public BigDecimal BALANCE;
}