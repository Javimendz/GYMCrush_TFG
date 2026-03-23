package com.backend.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS para la API
 * Permite solicitudes desde los orígenes configurados en el archivo .env
 * y expone el encabezado Authorization para autenticación.
 */
@Configuration
public class Webconfig {

    @Value("${app.cors.origins}")
    private String[] allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Solo protegemos la API
                        .allowedOrigins(allowedOrigins) // Orígenes desde .env
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*") // Permite cualquier cabecera (incluido Authorization)
                        .allowCredentials(true) //  OBLIGATORIO si usas cookies o Auth de navegador
                        .maxAge(3600); // Cachea la respuesta de "pre-flight" por 1 hora
            }
        };
    }
}