package com.leon.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/sw.js")
                .addResourceLocations("classpath:/static/sw.js")
                .setCachePeriod(0); // Do not cache service worker
        
        registry.addResourceHandler("/manifest.json")
                .addResourceLocations("classpath:/static/manifest.json");
    }
}
