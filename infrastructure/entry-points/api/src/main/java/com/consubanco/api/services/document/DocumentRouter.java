package com.consubanco.api.services.document;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DocumentRouter {

    @Value("${entry.api.path-services.document}")
    private String serviceBasePath;

    @Bean
    public RouterFunction<ServerResponse> documentRoutes(DocumentHandler handler) {
        return RouterFunctions.nest(
                path(serviceBasePath).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                route()
                    .POST(handler::generateDocument, DocumentOpenAPI.generateDocument())
                    .build()
        );
    }

}
