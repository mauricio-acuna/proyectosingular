package com.aireadiness.common.config;

import com.aireadiness.auth.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Application configuration for data initialization
 * Handles startup tasks like creating default admin user
 */
@Configuration
public class DataInitializationConfig {
    
    /**
     * Initialize default data on application startup
     * Only runs in development and production profiles
     */
    @Bean
    @Profile({"dev", "prod", "default"})
    public CommandLineRunner initializeDefaultData(AuthService authService) {
        return args -> {
            // Create default admin user if none exists
            authService.createDefaultAdminIfNotExists();
        };
    }
}
