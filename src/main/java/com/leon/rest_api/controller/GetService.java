//package com.leon.rest_api.controller;
//
//import com.leon.rest_api.utils.CommonHashMap;
//import org.springframework.transaction.annotation.Transactional;
//
//public abstract class GetService<I> {
//
//    public enum dto {
//        INPUT,
//        OUTPUT
//    }
//
//    @Transactional
//    public CommonHashMap run(I input) throws Exception {
//        return executeProcess(input);
//    }
//
//    public abstract CommonHashMap executeProcess(I input) throws Exception;
//    abstract public Class<?> getDtoClass(Enum i);
//
//}
