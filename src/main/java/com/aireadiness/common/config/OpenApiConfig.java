package com.aireadiness.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 configuration for API documentation
 * Based on PRD requirements for API documentation
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI aiReadinessOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Readiness Web API")
                        .description("API para autoevaluación de empleabilidad en la era de la IA")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AI Readiness Team")
                                .url("https://github.com/mauricio-acuna/proyectosingular")
                                .email("contact@aireadiness.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server"),
                        new Server().url("https://api.aireadiness.com").description("Production server")
                ));
    }
}
