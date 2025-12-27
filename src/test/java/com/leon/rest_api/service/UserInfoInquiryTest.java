//package com.leon.rest_api.service;
//
//import com.leon.rest_api.dto.UserInfoInquiryDTOInput;
//import com.leon.rest_api.entities.UserInfo;
//import com.leon.rest_api.repository.UserInfoRepository;
//import com.leon.rest_api.utils.CommonHashMap;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class UserInfoInquiryTest {
//    private static final Logger logger = LoggerFactory.getLogger(UserInfoInquiryTest.class);
//
//    @InjectMocks
//    private UserInfoInquiry userInfoInquiry;
//
//    @Mock
//    private UserInfoRepository userInfoRepository;
//
//    @Test
//    public void testExecuteProcess_userFound() throws Exception {
//        // mock data
//        UserInfo mockUser = new UserInfo();
//        mockUser.setUserId(new BigDecimal("123"));
//        mockUser.setUsername("Leon");
//
//        when(userInfoRepository.findByUserId(new BigDecimal("123")))
//                .thenReturn(Optional.of(mockUser));
//
//        UserInfoInquiryDTOInput input = new UserInfoInquiryDTOInput();
//        input.USERID = new BigDecimal("123");
//
//        userInfoInquiry.setInput(input);
//
//        // Act
//        CommonHashMap result = userInfoInquiry.run();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("Leon", result.getString("USERNAME"));
//    }
//
//    @Test
//    public void testExecuteProcess_userNotFound_shouldThrow() {
//        // Arrange
//        when(userInfoRepository.findByUserId(any()))
//                .thenReturn(Optional.empty());
//
//        UserInfoInquiryDTOInput input = new UserInfoInquiryDTOInput();
//        input.USERID = new BigDecimal("999");
//
//        userInfoInquiry.setInput(input);
//
//        // Act & Assert
//        Exception exception = assertThrows(Exception.class, () -> userInfoInquiry.run());
//        assertTrue(exception.getMessage().contains("Data Not Found"));
//    }
//}
//
