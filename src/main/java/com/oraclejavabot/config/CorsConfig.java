package com.oraclejavabot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // 🔥 Permite múltiples orígenes (local + OCI + futuros dominios)
        config.setAllowedOriginPatterns(List.of("*"));

        // 🔥 IMPORTANTE: incluir PATCH y OPTIONS
        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
        ));

        // 🔥 Permitir todos los headers
        config.setAllowedHeaders(List.of("*"));

        // 🔥 Exponer headers (por si usas Authorization/JWT en response)
        config.setExposedHeaders(List.of("*"));

        // 🔥 Necesario si usas JWT / cookies / auth headers
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}