//package com.leon.rest_api.logger;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Component
//public class LoggingInterceptor implements HandlerInterceptor {
//    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        log.info("========= Incoming : {} {} =========", request.getMethod(), request.getRequestURL());
//        return true; // continue processing
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        log.info("========= Completed: {} {} | status {} =========", request.getMethod(), request.getRequestURL(), response.getStatus());
//        log.info("{} {}: {}", request.getMethod(), request.getRequestURL(), response);
//    }
//}
//
