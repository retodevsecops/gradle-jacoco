package com.consubanco.api.services.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class FileRouter {

    private static final String CNCA_PATH = "/cnca/offer";
    @Value("${entry.api.path-services.file}")
    private String fileServicesPath;

    @Bean
    public RouterFunction<ServerResponse> fileRoutes(FileHandler handler) {
        return RouterFunctions.nest(
                path(fileServicesPath).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                route()
                    .POST(CNCA_PATH, handler::buildCNCALetters, FileOpenAPI.buildCNCALetters())
                    .build()
        );
    }

}
