package com.consubanco.consumer.services;

import com.consubanco.consumer.adapters.document.properties.ApisProperties;
import com.consubanco.consumer.config.dto.RestConsumerLogDTO;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.loan.constant.OfferStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.consubanco.consumer.commons.ClientExceptionFactory.requestError;
import static com.consubanco.consumer.commons.ClientExceptionFactory.responseError;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.*;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Service
public class OfferApiService {

    private final WebClient renexClient;
    private final ApisProperties apis;
    private final CustomLogger logger;

    public OfferApiService(final @Qualifier("ApiRenexClient") WebClient renexClient,
                           final ApisProperties apisProperties,
                           final CustomLogger logger) {
        this.renexClient = renexClient;
        this.apis = apisProperties;
        this.logger = logger;
    }

    @Cacheable("offers")
    public Mono<Map<String, Object>> activeOfferByProcess(String processId) {
        return this.renexClient.get()
                .uri(apis.getRenex().getApiActiveOffer(), processId)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    HttpStatusCode status = response.statusCode();
                    if (status == NO_CONTENT || status == CONFLICT)
                        return Mono.empty();
                    if (status.is2xxSuccessful())
                        return response.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
                    return response.createException()
                            .flatMap(Mono::error);
                })
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .onErrorMap(WebClientResponseException.class, error -> responseError(error, API_ACTIVE_OFFER_ERROR))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(API_ACTIVE_OFFER_ERROR));
    }

    @CacheEvict("offers")
    public Mono<String> acceptOffer(String processId) {
        return this.renexClient.post()
                .uri(apis.getRenex().getApiAcceptOffer(), processId)
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .filter(HttpStatusCode::is2xxSuccessful)
                .map(httpStatusCode -> OfferStatus.FINISHED.name())
                .switchIfEmpty(Mono.just(OfferStatus.ERROR.name()))
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .doOnError(WebClientResponseException.class, error -> logger.error(new RestConsumerLogDTO(error)))
                .doOnError(error -> !(error instanceof WebClientResponseException), logger::error)
                .onErrorResume(e -> Mono.just(OfferStatus.ERROR.name()));
    }

    public Mono<String> getOfferHealth() {
        return this.renexClient.get()
                .uri(apis.getRenex().getApiHealthOffer())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .onErrorMap(throwTechnicalError(OFFER_HEALTH_ERROR));
    }

}
