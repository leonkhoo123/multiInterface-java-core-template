//package com.leon.rest_api.service.ob;
//
//import com.leon.rest_api.entities.UserInfo;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.math.BigDecimal;
//
//public class UserInfoInquiryOB implements UserInfoInquiryOBInterface {
//    private static final Logger logger = LoggerFactory.getLogger(UserInfoInquiryOB.class);
//
//    private final UserInfoInquiryRepos repo;
//
//    private UserInfo userInfo;
//
//    public UserInfoInquiryOB(UserInfoInquiryRepos repo) {
//        this.repo = repo;
//    }
//
//    public UserInfo getUserInfo(BigDecimal userId) throws Exception{
//        if (userInfo==null){
//            userInfo = repo.userInfoRepository.findByUserId(userId)
//                    .orElseThrow(() -> new Exception("User Not Found"));
//        }
//        return userInfo;
//    }
//}
