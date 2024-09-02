package com.consubanco.api.services.management;

import com.consubanco.api.services.management.constants.ManagementPaths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class ManagementRouter {

    @Value("${entry.api.path-services.management}")
    private String managementServicesPath;

    @Bean
    public RouterFunction<ServerResponse> managementRoutes(ManagementHandler handler) {
        return RouterFunctions.nest(
                path(managementServicesPath).and(accept(APPLICATION_JSON)),
                route()
                    .GET(ManagementPaths.CACHE, handler::getItemsCache, ManagementOpenAPI.getItemsCache())
                    .DELETE(ManagementPaths.CACHE, handler::cleanCache, ManagementOpenAPI.cleanCache())
                .build()
        );
    }

}
