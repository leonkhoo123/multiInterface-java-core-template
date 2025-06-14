package com.leon.rest_api.controller;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonApiController {

    @Autowired
    private ApplicationContext context;

    @RequestMapping("/{processName}")
    public String serve(@PathVariable String processName) {
        try {
            RestService api = (RestService) context.getBean(processName);
            return api.serve();
        } catch (BeansException e) {
            return "No process found for name: " + processName;
        }
    }
}


