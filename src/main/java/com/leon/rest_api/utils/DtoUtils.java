package com.leon.rest_api.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class DtoUtils {

    /**
     * Normalize null fields in a DTO:
     * - Strings → ""
     * - Numeric types → 0
     * - Leave other objects as null
     */
    public static <T> T cleanDto(T dto) {
        if (dto == null) return null;

        Field[] fields = dto.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(dto);
                if (value == null) {
                    Class<?> type = field.getType();
                    if (type.equals(String.class)) {
                        field.set(dto, "");
                    } else if (type.equals(BigDecimal.class)) {
                        field.set(dto, BigDecimal.ZERO);
                    } else if (type.equals(Long.class)) {
                        field.set(dto, 0L);
                    } else if (type.equals(Integer.class) || type.equals(int.class)) {
                        field.set(dto, 0);
                    } else if (type.equals(Double.class) || type.equals(double.class)) {
                        field.set(dto, 0d);
                    }
                    // else leave null
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to clean DTO", e);
            }
        }
        return dto;
    }
}
