package com.leon.common.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CommonHashMap {
	
	public CommonHashMap() {}
	
	private HashMap<String,Object> commonHmap = new HashMap<>();
	
	public void setHmap(Object a) {
        if (a == null) return;

        Field[] fields = a.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(a);
                String fieldName = field.getName();

                if (value != null) {
                    commonHmap.put(fieldName.toUpperCase(), value);
                } else {
                    Class<?> type = field.getType();

                    if (type == String.class) {
                        commonHmap.put(fieldName.toUpperCase(), "");
                    } else if (type == BigDecimal.class) {
                        commonHmap.put(fieldName.toUpperCase(), BigDecimal.ZERO);
                    } else {
                        commonHmap.put(fieldName.toUpperCase(), null); // fallback for other types
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to access field: " + field.getName(), e);
            }
        }
    }
	
	public void setHmap(CommonHashMap a) {
	    if (a == null || a.getHmap() == null) return;

	    commonHmap.putAll(a.getHmap());
	}
	
	public void setHmap(Map<String, Object> a) {
	    commonHmap.putAll(a);
	}
	
	public void updateHmap(Object a) {
	    if (a == null) return;

	    Field[] fields = a.getClass().getDeclaredFields();
	    for (Field field : fields) {
	        field.setAccessible(true);
	        String key = field.getName();

	        if (commonHmap.containsKey(key)) {
	            try {
	                Object value = field.get(a);
	                commonHmap.put(key, value);
	            } catch (IllegalAccessException e) {
	                throw new RuntimeException("Unable to access field: " + key, e);
	            }
	        }
	    }
	}
	
	public void updateHmap(CommonHashMap a) {
	    if (a == null || a.getHmap() == null) return;

	    for (Map.Entry<String, Object> entry : a.getHmap().entrySet()) {
	        String key = entry.getKey();
	        if (commonHmap.containsKey(key)) {
	            commonHmap.put(key, entry.getValue());
	        }
	    }
	}


	public HashMap<String,Object> getHmap() {
		return commonHmap;
	}
	
	public void setBigDecimal(String key, BigDecimal value) {
		commonHmap.put(key, value);
	}
	
	public BigDecimal getBigDecimal(String key) throws Exception {
		
		if(!(commonHmap.get(key) instanceof BigDecimal)) {
			throw new Exception("Data for "+key+" is not BigDecimal");

		}else {
			return (BigDecimal) commonHmap.get(key);
		}
	}
	
	public void setString(String key, String value) {
	    commonHmap.put(key, value);
	}

	public String getString(String key) throws Exception {
	    Object val = commonHmap.get(key);
	    if (!(val instanceof String)) {
	        throw new Exception("Data for " + key + " is not String");
	    }
	    return (String) val;
	}
	
	@Override
	public String toString(){
	    try {
	    	ObjectMapper mapper = new ObjectMapper();
	        mapper.registerModule(new JavaTimeModule());
	        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	        return mapper.writeValueAsString(commonHmap);
	    } catch (JsonProcessingException e) {
	    	System.out.println(e);
	    	return "{}";
	    }
	}


}
