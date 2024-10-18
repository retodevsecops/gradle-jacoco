package com.consubanco.api.services.rpa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.consubanco.api.services.rpa.RpaOpenAPI.uploadCartaLibranza;
import static com.consubanco.api.services.rpa.RpaOpenAPI.uploadSipreSimulation;
import static com.consubanco.api.services.rpa.constants.RpaPaths.CARTA_LIBRANZA_PATH;
import static com.consubanco.api.services.rpa.constants.RpaPaths.SIPRE_SIMULATION_PATH;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class RpaRouter {

    @Value("${entry.api.path-services.rpa}")
    private String servicesPath;

    @Bean
    public RouterFunction<ServerResponse> rpaRoutes(RpaHandler handler) {
        return RouterFunctions.nest(
                path(servicesPath).and(accept(APPLICATION_JSON)),
                route()
                    .POST(CARTA_LIBRANZA_PATH, handler::uploadCartaLibranza, uploadCartaLibranza())
                    .POST(SIPRE_SIMULATION_PATH, handler::uploadSipreSimulation, uploadSipreSimulation())
                .build()
        );
    }

}
