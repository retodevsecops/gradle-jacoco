package com.consubanco.consumer.services.nom151;

import com.consubanco.consumer.services.nom151.util.ApiResponseNom151Util;
import com.consubanco.consumer.services.nom151.util.GetNom151Util;
import com.consubanco.consumer.services.nom151.util.GetSignedDocumentUtil;
import com.consubanco.consumer.services.nom151.util.LoadDocumentReqDTO;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.consubanco.consumer.services.nom151.util.GetSignedDocumentUtil.buildRequest;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.API_NOM151_ERROR;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.API_NOM151_RESPONSE_ERROR;

@Service
public class Nom151ApiService {

    private static final String SOAP_ACTION = "SOAPAction";
    private final WebClient nom151Client;
    private final Nom151ApiProperties properties;

    public Nom151ApiService(final @Qualifier("nom151Client") WebClient nom151Client,
                            final Nom151ApiProperties apisProperties) {
        this.nom151Client = nom151Client;
        this.properties = apisProperties;
    }

    public Mono<Boolean> loadDocumentForCSB(LoadDocumentReqDTO loadDocumentReqDTO) {
        return loadDocument(loadDocumentReqDTO, properties.getUserCSB(), properties.getPasswordCSB());
    }

    public Mono<Boolean> loadDocumentForMN(LoadDocumentReqDTO loadDocumentReqDTO) {
        return loadDocument(loadDocumentReqDTO, properties.getUserMN(), properties.getPasswordMN());
    }

    public Mono<String> getSignedDocumentForCSB(String documentId) {
        return getSignedDocument(properties.getUserCSB(), properties.getPasswordCSB(), documentId);
    }

    public Mono<String> getSignedDocumentForMN(String documentId) {
        return getSignedDocument(properties.getUserMN(), properties.getPasswordMN(), documentId);
    }

    public Mono<String> getNom151ForCSB(String documentId) {
        return getNom151(properties.getUserCSB(), properties.getPasswordCSB(), documentId);
    }

    public Mono<String> getNom151ForMN(String documentId) {
        return getNom151(properties.getUserMN(), properties.getPasswordMN(), documentId);
    }

    private Mono<Boolean> loadDocument(LoadDocumentReqDTO loadDocumentReqDTO, String user, String password) {
        return nom151Client.post()
                .uri(properties.getEndpoint())
                .header(SOAP_ACTION, properties.getActions().getLoadDocument())
                .bodyValue(loadDocumentReqDTO.buildRequest(user, password))
                .exchangeToMono(response -> getResponse(response, LoadDocumentReqDTO::getSuccessfulResponse))
                .map(LoadDocumentReqDTO::resultIsSuccess)
                .onErrorMap(e -> !(e instanceof TechnicalException), error -> buildTechnical(error, API_NOM151_ERROR));
    }

    /**
     * TODO: agregar logs
     *
     * @param user
     * @param password
     * @param documentId
     * @return
     */
    private Mono<String> getSignedDocument(String user, String password, String documentId) {
        String bodyValue = buildRequest(user, password, documentId);
        return nom151Client.post()
                .uri(properties.getEndpoint())
                .header(SOAP_ACTION, properties.getActions().getGetDocumentSigned())
                .bodyValue(bodyValue)
                .exchangeToMono(response -> getResponse(response, GetSignedDocumentUtil::getSuccessfulResponse))
                .onErrorMap(e -> !(e instanceof TechnicalException), error -> buildTechnical(error, API_NOM151_ERROR));
    }

    private Mono<String> getNom151(String user, String password, String documentId) {
        return nom151Client.post()
                .uri(properties.getEndpoint())
                .header(SOAP_ACTION, properties.getActions().getGetNom151())
                .bodyValue(GetNom151Util.buildRequest(user, password, documentId))
                .exchangeToMono(response -> getResponse(response, GetNom151Util::getSuccessfulResponse))
                .onErrorMap(e -> !(e instanceof TechnicalException), error -> buildTechnical(error, API_NOM151_ERROR));
    }

    private Mono<String> getResponse(ClientResponse response, Function<String, String> getSuccessResult) {
        return response.bodyToMono(String.class)
                .map(responseAsString -> {
                    if (response.statusCode().is2xxSuccessful()) return getSuccessResult.apply(responseAsString);
                    String errorDetail = ApiResponseNom151Util.getErrorDetail(responseAsString);
                    throw ExceptionFactory.buildTechnical(errorDetail, API_NOM151_RESPONSE_ERROR);
                });
    }

    public Integer getValidDays() {
        return properties.getValidTimeMin();
    }

}
