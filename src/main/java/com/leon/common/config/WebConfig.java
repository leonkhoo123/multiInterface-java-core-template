package com.leon.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve sw.js from /js/sw.js but expose it at /sw.js (or /js/sw.js depending on scope needs)
        // Service Workers have a scope. If served from /js/sw.js, it can only control pages under /js/.
        // To control /web/, it usually needs to be served from root or have a Service-Worker-Allowed header.
        // However, since you moved it to /js/sw.js, let's assume you want to access it there.
        // BUT: If your pages are in /web/, a SW at /js/sw.js CANNOT control them by default.
        // It is best to map a root URL to the file location.
        
        // Mapping /sw.js URL to the physical file at classpath:/static/js/sw.js
        registry.addResourceHandler("/sw.js")
                .addResourceLocations("classpath:/static/js/sw.js")
                .setCachePeriod(0); 

        // Mapping /manifest.json URL to the physical file at classpath:/static/web/manifest.json
        // (Assuming you want it accessible at root or /web/manifest.json - let's keep your HTML links working)
        // Your HTML links point to /web/manifest.json, so we don't strictly need a custom handler if Spring Boot's default static resource handling works for /web/**.
        // But explicit mapping is safer if you want specific caching behavior.
        registry.addResourceHandler("/web/manifest.json")
                .addResourceLocations("classpath:/static/web/manifest.json");
    }
}
