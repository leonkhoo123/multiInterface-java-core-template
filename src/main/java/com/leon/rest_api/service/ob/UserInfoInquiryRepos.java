package com.leon.rest_api.service.ob;

import com.leon.rest_api.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UserInfoInquiryRepos {
    @Autowired
    public UserInfoRepository userInfoRepository;

    public UserInfoInquiryOB createOB() {
        return new UserInfoInquiryOB(this);
    }
}
