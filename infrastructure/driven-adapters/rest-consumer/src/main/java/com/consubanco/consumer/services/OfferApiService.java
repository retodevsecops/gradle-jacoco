package com.consubanco.consumer.services;

import com.consubanco.consumer.adapters.document.properties.PayloadApisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.API_ACTIVE_OFFER_ERROR;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.OFFER_HEALTH_ERROR;

@Service
public class OfferApiService {

    private final WebClient renexClient;
    private final PayloadApisProperties apis;

    public OfferApiService(final @Qualifier("ApiRenexClient") WebClient renexClient,
                           final PayloadApisProperties apisProperties) {
        this.renexClient = renexClient;
        this.apis = apisProperties;
    }

    @Cacheable("offers")
    public Mono<Map<String, Object>> activeOfferByProcess(String processId) {
        return this.renexClient.get()
                .uri(apis.getRenex().getApiActiveOffer(), processId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorMap(throwTechnicalError(API_ACTIVE_OFFER_ERROR));
    }

    public Mono<String> getOfferHealth() {
        return this.renexClient.get()
                .uri(apis.getRenex().getApiHealthOffer())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorMap(throwTechnicalError(OFFER_HEALTH_ERROR));
    }

}
