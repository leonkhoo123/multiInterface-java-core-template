//package com.leon.rest_api.logger;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
//@ControllerAdvice
//public class ResponseLogger implements ResponseBodyAdvice<Object> {
//
//    private static final Logger log = LoggerFactory.getLogger(ResponseLogger.class);
//
//    @Override
//    public boolean supports(MethodParameter returnType, Class converterType) {
//        return true;
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object body, MethodParameter returnType,
//                                  MediaType selectedContentType,
//                                  Class selectedConverterType,
//                                  ServerHttpRequest request,
//                                  ServerHttpResponse response) {
//        log.info("{} {}: {}", request.getMethod(), request.getURI(), body);
//        return body;
//    }
//}
