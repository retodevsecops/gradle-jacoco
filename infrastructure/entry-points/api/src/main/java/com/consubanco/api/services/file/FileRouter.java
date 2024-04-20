package com.consubanco.api.services.file;

import com.consubanco.api.services.file.handlers.OfferFileHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.consubanco.api.services.file.FileOpenAPI.*;
import static com.consubanco.api.services.file.constants.FilePaths.*;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class FileRouter {

    @Value("${entry.api.path-services.file}")
    private String fileServicesPath;

    @Bean
    public RouterFunction<ServerResponse> fileRoutes(FileHandler handler, OfferFileHandler offerFileHandler) {
        return RouterFunctions.nest(
                path(fileServicesPath).and(accept(APPLICATION_JSON)),
                route()
                    .GET(CNCA_PATH, handler::buildCNCALetters, buildCNCALetters())
                    .POST(GENERATE_DOCUMENT_PATH, handler::generateFileWithDocuments, generateFileWithDocuments())
                    .POST(GENERATE_DOCUMENT_ENCODED_PATH, handler::generateFileEncoded, generateFileEncoded())
                    .POST(GET_AND_UPLOAD_DOCUMENT_PATH, handler::getAndUpload, getAndUpload())
                    .POST(DOCUMENTS_AGREEMENT_PATH, contentType(MULTIPART_FORM_DATA), handler::uploadAgreementFiles, uploadAgreementFiles())
                    .GET(FILES_OFFER_PATH, offerFileHandler::getFilesByOffer, getFilesByOffer())
                    .GET(FILES_CUSTOMER_VIEW_PATH, offerFileHandler::getCustomerVisibleFiles, getCustomerVisibleFiles())
                    .POST(PAYLOAD_TEMPLATE_PATH, contentType(MULTIPART_FORM_DATA), handler::uploadPayloadTemplate, uploadPayloadTemplate())
                    .POST(AGREEMENTS_CONFIG_PATH, contentType(MULTIPART_FORM_DATA), handler::uploadAgreementsConfig, uploadAgreementsConfig())
               .build()
        );
    }

}
