package com.leon.rest_api.dto.response;

import java.math.BigDecimal;

public class UserInfoInquiryResponse {
    private BigDecimal userid;
    private String username;
    private BigDecimal balance;

    public UserInfoInquiryResponse() {
    }

    public UserInfoInquiryResponse(BigDecimal userid, String username, BigDecimal balance) {
        this.userid = userid;
        this.username = username;
        this.balance = balance;
    }

    public BigDecimal getUserid() {
        return userid;
    }

    public void setUserid(BigDecimal userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}