package com.leon.rest_api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class CommonDTOUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T populateDefaults(T dto) throws Exception{
        if (dto == null) {
            return null;
        }

        for (Field field : dto.getClass().getFields()) {
            try {
                Object value = field.get(dto);

                if (value == null) {
                    if (field.getType().equals(String.class)) {
                        field.set(dto, "");
                    } else if (field.getType().equals(BigDecimal.class)) {
                        field.set(dto, BigDecimal.ZERO);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new Exception("[populateDefaults] method error:",e);
            }
        }
        return dto;
    }


    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }
}


