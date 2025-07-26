package com.leon.rest_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class ControllerService {
    private static final Logger logger = LoggerFactory.getLogger(ControllerService.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper mapper;

    public Object serve(String processName, String input) throws Exception {
        RestService api = (RestService) context.getBean(processName);
        Class<?> dtoClass = api.getDtoClass("i");
        Object dtoObj = mapper.readValue(input, dtoClass);
        api.setInput(populateDefaults(dtoObj));

        HashMap<String, Object> output = api.run().getHmap();
        Class<?> outputDtoClass = api.getDtoClass("o");
        Object outputDto = mapper.convertValue(output, outputDtoClass);
        return populateDefaults(outputDto);
    }

    public static Object populateDefaults(Object dto) {
        for (Field field : dto.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(dto);

                if (value == null) {
                    if (field.getType().equals(String.class)) {
                        field.set(dto, "");
                    } else if (field.getType().equals(BigDecimal.class)) {
                        field.set(dto, BigDecimal.ZERO);
                    }
                }
            } catch (IllegalAccessException e) {
                // Optional: log or rethrow
                e.printStackTrace();
            }
        }
        return dto;
    }
}

