package com.leon.rest_api.controller;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@RestController
public class CommonApiController {

    @Autowired
    private ApplicationContext context;

    @RequestMapping("/{processName}")
    public String serve(@PathVariable String processName,@RequestBody String input) {
        try {
            RestService api = (RestService) context.getBean(processName);
            Class<?> dtoClass = api.getDtoClass("i");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Object dtoObj = mapper.readValue(input, dtoClass);
            
            // Convert DTO to Map<String, Object>
            @SuppressWarnings("unchecked")
            Map<String, Object> inputMap = mapper.convertValue(populateDefaults(dtoObj), Map.class);
            api.setInput(inputMap);
            return api.run().toString();
        } catch (Exception e) {
			// TODO Auto-generated catch block
        	e.printStackTrace();
			return "Error : " + e.getMessage();
		}
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


