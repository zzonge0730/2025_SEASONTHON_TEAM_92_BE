package com.tenantcollective.rentnegotiation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:3000", 
                    "http://localhost:3001",
                    "http://127.0.0.1:3000",
                    "http://127.0.0.1:3001",
                    "http://172.21.135.200:3000",
                    "https://d3b5339e4b2d.ngrok-free.app",
                    "https://2025-seasonthon-team-92-fe.vercel.app",
                    "https://houselent.vercel.app",
                    "https://houselent-3srqcm2ee-woohyeok-kangs-projects.vercel.app",
                    "https://*.vercel.app",
                    "https://*.ngrok-free.app",
                    "https://*.ngrok.io"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000", 
            "http://localhost:3001",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:3001",
            "http://172.21.135.200:3000",
            "https://d3b5339e4b2d.ngrok-free.app",
            "https://2025-seasonthon-team-92-fe.vercel.app",
            "https://houselent.vercel.app",
            "https://houselent-3srqcm2ee-woohyeok-kangs-projects.vercel.app",
            "https://*.vercel.app",
            "https://*.ngrok-free.app",
            "https://*.ngrok.io"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}