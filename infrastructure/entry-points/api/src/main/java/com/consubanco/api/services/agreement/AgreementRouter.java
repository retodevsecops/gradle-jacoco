package com.consubanco.api.services.agreement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.consubanco.api.services.agreement.AgreementOpenAPI.*;
import static com.consubanco.api.services.agreement.constants.AgreementPaths.*;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class AgreementRouter {

    @Value("${entry.api.path-services.agreement}")
    private String agreementServicesPath;

    @Bean
    public RouterFunction<ServerResponse> agreementRoutes(AgreementHandler handler) {
        return RouterFunctions.nest(
                path(agreementServicesPath).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                route()
                        .GET(AGREEMENT_NUMBER_PATH_PARAM, handler::findByNumber, findByNumber())
                        .GET(ATTACHMENTS_PATH, handler::getAttachments, getAttachments())
                        .POST(GENERATE_DOCUMENTS_PATH, handler::generateDocuments, generateDocuments())
                        .build()
        );
    }

}
