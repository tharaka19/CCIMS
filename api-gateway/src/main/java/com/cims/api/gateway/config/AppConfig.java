package com.cims.api.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class that provides Spring beans for use throughout the application.
 */
@Configuration
public class AppConfig {

    /**
     * Creates and returns a new instance of the {@link RestTemplate} class.
     *
     * @return a new {@link RestTemplate} instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
