package com.leon.rest_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.rest_api.utils.DTOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ControllerService {
    private static final Logger logger = LoggerFactory.getLogger(ControllerService.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper mapper;

    public Object postServe(String processName, Map<String, Object> input) throws Exception {
        PostService api = (PostService) context.getBean(processName);
        Class<?> inputDtoClass = api.getDtoClass(PostService.dto.INPUT);

        // Convert the generic Map directly into DTO
        Object inputDtoObj = mapper.convertValue(input, inputDtoClass);
        api.setInput(DTOUtils.populateDefaults(inputDtoObj));

        HashMap<String, Object> output = api.run().getHmap();
        Class<?> outputDtoClass = api.getDtoClass(PostService.dto.OUTPUT);
        Object outputDto = mapper.convertValue(output, outputDtoClass);

        return DTOUtils.populateDefaults(outputDto);
    }


    public Object getServe(String processName, Map<String, String> params) throws Exception {
        GetService api = (GetService) context.getBean(processName);
        Class<?> inputDtoClass = api.getDtoClass(GetService.dto.INPUT);
        // Convert query params (Map<String, String>) into input DTO
        Object inputDtoObj = mapper.convertValue(params, DTOUtils.populateDefaults(inputDtoClass));

        HashMap<String, Object> output = api.run(DTOUtils.populateDefaults(inputDtoObj)).getHmap();
        Class<?> outputDtoClass = api.getDtoClass(GetService.dto.OUTPUT);
        Object outputDto = mapper.convertValue(output, outputDtoClass);

        return DTOUtils.populateDefaults(outputDto);
    }

    private static Object getInputDtoObj(Object inputDtoObj) {
        return inputDtoObj;
    }
}

