package com.leon.rest_api.dto.request;

import java.math.BigDecimal;


public class UserInfoInquiryRequest {
	private BigDecimal userid;

	public UserInfoInquiryRequest(BigDecimal userid) {
		this.userid = userid;
	}

	public BigDecimal getUserid() {
		return userid;
	}

	public void setUserid(BigDecimal userid) {
		this.userid = userid;
	}
}