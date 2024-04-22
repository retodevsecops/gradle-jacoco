package com.consubanco.consumer.adapters.document;

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
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.API_CUSTOMER_ERROR;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.CUSTOMER_HEALTH_ERROR;

@Service
public class CustomerApiConsumer {

    private final WebClient renexClient;
    private final PayloadApisProperties apis;

    public CustomerApiConsumer(final @Qualifier("ApiRenexClient") WebClient renexClient,
                               final PayloadApisProperties apisProperties) {
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
                .onErrorMap(throwTechnicalError(API_CUSTOMER_ERROR));
    }

    public Mono<String> getCustomerHealth() {
        return this.renexClient.get()
                .uri(apis.getRenex().getApiHealthCustomer())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorMap(throwTechnicalError(CUSTOMER_HEALTH_ERROR));
    }

}
