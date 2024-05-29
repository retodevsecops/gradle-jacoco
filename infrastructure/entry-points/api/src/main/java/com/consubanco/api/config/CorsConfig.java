package com.consubanco.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class CorsConfig implements WebFluxConfigurer {

    private static final String PATHS = "/**";
    private static final String ALL = "*";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(PATHS)
                .allowedOrigins(ALL)
                .allowedMethods(ALL)
                .allowedHeaders(ALL)
                .maxAge(3600);
    }
}
