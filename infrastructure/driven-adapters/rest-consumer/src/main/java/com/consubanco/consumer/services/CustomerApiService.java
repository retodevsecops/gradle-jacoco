package com.consubanco.consumer.services;

import com.consubanco.consumer.adapters.document.properties.ApisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.*;

@Service
public class CustomerApiService {

    private final WebClient renexClient;
    private final ApisProperties apis;

    public CustomerApiService(final @Qualifier("ApiRenexClient") WebClient renexClient,
                              final ApisProperties apisProperties) {
        this.renexClient = renexClient;
        this.apis = apisProperties;
    }

    @Cacheable("customers")
    public Mono<Map<String, Object>> customerDataByProcess(String processId) {
        return this.renexClient.get()
                .uri(apis.getRenex().getApiCustomerProcess(), processId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_CUSTOMER_ERROR))
                .onErrorMap(throwTechnicalError(API_CUSTOMER_ERROR));
    }
    public Mono<Map<String, Object>> customerBiometricValidation(String processId) {
        return this.renexClient.get()
                .uri(apis.getRenex().getApiCustomerBiometricValidation(), processId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_BIOMETRIC_TASK))
                .onErrorMap(throwTechnicalError(API_BIOMETRIC_TASK));
    }

    public Mono<String> getCustomerHealth() {
        return this.renexClient.get()
                .uri(apis.getRenex().getApiHealthCustomer())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorMap(throwTechnicalError(CUSTOMER_HEALTH_ERROR));
    }

}
