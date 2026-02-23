package com.example.studyrats.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TestReportConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/public/test-report/**")
                .addResourceLocations("file:build/reports/tests/test/");

        registry.addResourceHandler("/public/coverage/**")
                .addResourceLocations("file:build/reports/jacoco/test/html/");
    }
}
