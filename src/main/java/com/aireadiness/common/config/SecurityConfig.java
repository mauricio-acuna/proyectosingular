package com.aireadiness.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
                // Public endpoints - Assessment Flow
                .requestMatchers("/api/v1/roles/**").permitAll()
                .requestMatchers("/api/v1/assessments/**").permitAll()
                .requestMatchers("/api/v1/reports/**").permitAll()
                
                // Health and monitoring
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**").permitAll()
                
                // Admin endpoints require authentication
                .requestMatchers("/api/admin/**").authenticated()
                .requestMatchers("/actuator/**").authenticated()
                
                // Static resources
                .requestMatchers("/", "/static/**", "/public/**").permitAll()
                
                // Allow all other requests for MVP
                .anyRequest().permitAll()
            )
            .httpBasic(httpBasic -> httpBasic.disable()) // Disable HTTP Basic for public endpoints
            .formLogin(form -> form.disable()) // Disable form login for API-only endpoints
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}
