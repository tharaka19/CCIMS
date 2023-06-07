package com.cims.equipment.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that provides Spring beans for use throughout the application.
 */
@Configuration
public class AppConfig {

    /**
     * Returns a new instance of {@link ModelMapper}, which is a flexible and
     * easy-to-use Java library for mapping objects between different representations
     * (e.g., DTOs and entities).
     *
     * @return a new {@link ModelMapper} instance.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
