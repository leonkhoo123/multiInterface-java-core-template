package com.leon.rest_api.config;

import com.leon.rest_api.logger.HttpLoggingFilter;
import com.leon.rest_api.security.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public HttpLoggingFilter httpLoggingFilter() {
        return new HttpLoggingFilter();
    }

    /**
     * Prevent Spring Boot from automatically registering HttpLoggingFilter as a global filter.
     * We only want it in the Security Filter Chain.
     */
    @Bean
    public FilterRegistrationBean<HttpLoggingFilter> httpLoggingFilterRegistration(HttpLoggingFilter filter) {
        FilterRegistrationBean<HttpLoggingFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * Prevent Spring Boot from automatically registering JwtAuthenticationFilter as a global filter.
     * We only want it in the Security Filter Chain.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
