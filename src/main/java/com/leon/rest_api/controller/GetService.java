package com.leon.rest_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.rest_api.utils.CommonHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.leon.rest_api.utils.DTOUtils.populateDefaults;

public abstract class GetService<I> {

    public enum dto {
        INPUT,
        OUTPUT
    }

    @Transactional
    public CommonHashMap run(I input) throws Exception {
        return executeProcess(input);
    }

    public abstract CommonHashMap executeProcess(I input) throws Exception;
    abstract public Class<?> getDtoClass(Enum i);

}
