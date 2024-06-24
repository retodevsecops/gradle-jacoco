package com.consubanco.api.services.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class HealthRouter {

    @Value("${entry.api.path-services.health}")
    private String healthServicePath;

    @Bean
    public RouterFunction<ServerResponse> healthRoutes(HealthHandler handler) {
        return RouterFunctions.nest(
                path(healthServicePath),
                route()
                        .GET(handler::health, HealthOpenAPI.health())
                        .build()
        );
    }

}
