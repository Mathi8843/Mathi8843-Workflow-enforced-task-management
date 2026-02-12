package com.example.TaskApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("*")       // allow any IP / domain
                .allowedMethods("*")       // GET, POST, PUT, DELETE, etc.
                .allowedHeaders("*")       // all headers
                .allowCredentials(false);  // must be false when using "*"
    }
}

