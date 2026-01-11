package com.leon.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve sw.js from root
        registry.addResourceHandler("/sw.js")
                .addResourceLocations("classpath:/static/sw.js")
                .setCachePeriod(0); 

        // Serve manifest.json from root
        registry.addResourceHandler("/manifest.json")
                .addResourceLocations("classpath:/static/manifest.json");
                
        // Serve favicon.ico from root
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/favicon.ico");
    }
}
