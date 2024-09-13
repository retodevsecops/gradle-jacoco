package com.consubanco.consumer.services.nom151;

import com.consubanco.consumer.services.nom151.util.ApiResponseNom151Util;
import com.consubanco.consumer.services.nom151.util.GetNom151Util;
import com.consubanco.consumer.services.nom151.util.GetSignedDocumentUtil;
import com.consubanco.consumer.services.nom151.util.LoadDocumentReqDTO;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.function.Function;

import static com.consubanco.consumer.commons.ClientExceptionFactory.requestError;
import static com.consubanco.consumer.services.nom151.util.GetSignedDocumentUtil.buildRequest;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.entities.document.message.DocumentMessage.retriesFailed;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.*;

@Service
public class Nom151ApiService {

    private static final String SOAP_ACTION = "SOAPAction";
    private final CustomLogger logger;
    private final WebClient nom151Client;
    private final Nom151ApiProperties properties;

    public Nom151ApiService(final @Qualifier("nom151Client") WebClient nom151Client,
                            final Nom151ApiProperties apisProperties,
                            final CustomLogger logger) {
        this.nom151Client = nom151Client;
        this.properties = apisProperties;
        this.logger = logger;
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

    public Integer getValidTime() {
        return properties.getValidTimeMin();
    }

    private Mono<Boolean> loadDocument(LoadDocumentReqDTO loadDocumentReqDTO, String user, String password) {
        String requestBody = loadDocumentReqDTO.buildRequest(user, password);
        logger.info("Request load document for nom151", requestBody);
        return nom151Client.post()
                .uri(properties.getEndpoint())
                .header(SOAP_ACTION, properties.getActions().getLoadDocument())
                .bodyValue(requestBody)
                .exchangeToMono(response -> getResponse(response, LoadDocumentReqDTO::getSuccessfulResponse))
                .map(LoadDocumentReqDTO::resultIsSuccess)
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .onErrorMap(e -> !(e instanceof TechnicalException), error -> buildTechnical(error, API_NOM151_ERROR));
    }

    private Mono<String> getSignedDocument(String user, String password, String documentId) {
        String requestBody = buildRequest(user, password, documentId);
        logger.info("Request get signed document for nom151", requestBody);
        String messageFormat = "Retry #%s for get signed document %s with error: %s";
        return callApiGetSignedDocument(requestBody)
                .retryWhen(Retry.fixedDelay(properties.getRetryStrategy().getMaxRetries(), properties.retryDelay())
                        .doBeforeRetry(signal -> {
                            long retries = signal.totalRetriesInARow();
                            String errorMessage = signal.failure().getMessage();
                            String message = String.format(messageFormat, retries, documentId, errorMessage);
                            logger.info(message);
                        }))
                .onErrorMap(e -> buildTechnical(retriesFailed(documentId, e.getMessage()), SIGNED_DOCUMENT_FAIL));
    }

    private Mono<String> callApiGetSignedDocument(String requestBody) {
        return nom151Client.post()
                .uri(properties.getEndpoint())
                .header(SOAP_ACTION, properties.getActions().getGetDocumentSigned())
                .bodyValue(requestBody)
                .exchangeToMono(response -> getResponse(response, GetSignedDocumentUtil::getSuccessfulResponse))
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .onErrorMap(e -> !(e instanceof TechnicalException), error -> buildTechnical(error, API_NOM151_ERROR));
    }

    private Mono<String> getNom151(String user, String password, String documentId) {
        String requestBody = GetNom151Util.buildRequest(user, password, documentId);
        logger.info("Request get nom151", requestBody);
        String messageFormat = "Retry #%s for get conservation certificate nom151 %s with error: %s";
        return callApiGetNom151(requestBody)
                .retryWhen(Retry.fixedDelay(properties.getRetryStrategy().getMaxRetries(), properties.retryDelay())
                        .doBeforeRetry(signal -> {
                            long retries = signal.totalRetriesInARow();
                            String errorMessage = signal.failure().getMessage();
                            String message = String.format(messageFormat, retries, documentId, errorMessage);
                            logger.info(message);
                        }))
                .onErrorMap(e -> buildTechnical(retriesFailed(documentId, e.getMessage()), SIGNED_DOCUMENT_FAIL));
    }

    private Mono<String> callApiGetNom151(String requestBody) {
        return nom151Client.post()
                .uri(properties.getEndpoint())
                .header(SOAP_ACTION, properties.getActions().getGetNom151())
                .bodyValue(requestBody)
                .exchangeToMono(response -> getResponse(response, GetNom151Util::getSuccessfulResponse))
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
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

}
