package com.oraclejavabot.features.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(Customizer.withDefaults())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // Login / auth
                .requestMatchers("/auth/**").permitAll()

                // Health checks para blue/green, NGINX, scripts, etc.
                .requestMatchers("/api/health").permitAll()

                // Frontend SPA y archivos estáticos
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/favicon.ico",
                    "/logo.svg",
                    "/assets/**",
                    "/*.svg",
                    "/*.css",
                    "/*.js",
                    "/*.png",
                    "/*.jpg",
                    "/*.jpeg",
                    "/*.webp",
                    "/*.ico"
                ).permitAll()

                // APIs protegidas
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/graphql").authenticated()

                // Rutas del frontend tipo /tareas, /equipos, /dashboard, etc.
                .anyRequest().permitAll()
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}