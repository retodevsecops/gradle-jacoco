package com.consubanco.api.services.ocr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.consubanco.api.services.ocr.OcrOpenAPI.*;
import static com.consubanco.api.services.ocr.constants.OcrPaths.FIND_BY_DOCUMENT_NAME_PATH;
import static com.consubanco.api.services.ocr.constants.OcrPaths.PROCESS_PATH;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class OcrRouter {

    @Value("${entry.api.path-services.ocr}")
    private String servicesPath;

    @Bean
    public RouterFunction<ServerResponse> ocrRoutes(OcrHandler handler) {
        return RouterFunctions.nest(
                path(servicesPath).and(accept(APPLICATION_JSON)),
                route()
                    .GET( handler::findByAnalysisId, findByAnalysisId())
                    .POST(PROCESS_PATH, handler::validateDocument, validateDocument())
                    .GET(PROCESS_PATH, handler::findByProcessId, findByProcessId())
                    .GET(FIND_BY_DOCUMENT_NAME_PATH, handler::findByDocumentName, findByDocumentName())
                .build()
        );
    }

}
