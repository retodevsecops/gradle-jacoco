package com.consubanco.consumer.services;

import com.consubanco.consumer.adapters.document.properties.ApisProperties;
import com.consubanco.consumer.config.dto.RestConsumerLogDTO;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.entities.loan.constant.OfferStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.API_ACTIVE_OFFER_ERROR;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.OFFER_HEALTH_ERROR;

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
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_ACTIVE_OFFER_ERROR))
                .onErrorMap(throwTechnicalError(API_ACTIVE_OFFER_ERROR));
    }

    public Mono<String> getOfferHealth() {
        return this.renexClient.get()
                .uri(apis.getRenex().getApiHealthOffer())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorMap(throwTechnicalError(OFFER_HEALTH_ERROR));
    }

    public Mono<String> acceptOffer(String processId) {
        return this.renexClient.post()
                .uri(apis.getRenex().getApiAcceptOffer(), processId)
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .filter(HttpStatusCode::is2xxSuccessful)
                .map(httpStatusCode -> OfferStatus.FINALIZED.name())
                .switchIfEmpty(Mono.just(OfferStatus.ERROR.name()))
                .doOnError(WebClientResponseException.class, error -> logger.error(new RestConsumerLogDTO(error)))
                .doOnError(error -> !(error instanceof WebClientResponseException), logger::error)
                .onErrorResume(e -> Mono.just(OfferStatus.ERROR.name()));
    }

}
