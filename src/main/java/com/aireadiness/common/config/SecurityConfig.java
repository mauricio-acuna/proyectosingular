package com.aireadiness.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for AI Readiness Web
 * Based on PRD security requirements
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/v1/roles/**").permitAll()
                .requestMatchers("/api/v1/assessments/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                
                // Admin endpoints require authentication
                .requestMatchers("/api/v1/admin/**").authenticated()
                .requestMatchers("/actuator/**").authenticated()
                
                // Allow all other requests for now (MVP)
                .anyRequest().permitAll()
            )
            .httpBasic(httpBasic -> {}); // Use HTTP Basic for admin endpoints
        
        return http.build();
    }
}
