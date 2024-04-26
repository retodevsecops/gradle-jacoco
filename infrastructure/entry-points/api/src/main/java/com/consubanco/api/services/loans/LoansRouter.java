package com.consubanco.api.services.loans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class LoansRouter {

    @Value("${entry.api.path-services.loans}")
    private String loansServicePath;

    @Bean
    public RouterFunction<ServerResponse> loansRoutes(LoansHandler handler) {
        return RouterFunctions.nest(
                path(loansServicePath),
                route()
                    .POST(handler::createApplication, LoansOpenAPI.createApplication())
                    .build()
        );
    }

}
