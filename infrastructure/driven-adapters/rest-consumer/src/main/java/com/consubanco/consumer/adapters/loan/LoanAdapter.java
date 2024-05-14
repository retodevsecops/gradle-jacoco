package com.consubanco.consumer.adapters.loan;

import com.consubanco.consumer.adapters.loan.properties.LoanApisProperties;
import com.consubanco.freemarker.ITemplateOperations;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.entities.loan.gateway.LoanGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.API_PROMOTER_ERROR;

@Service
public class LoanAdapter implements LoanGateway {

    private final CustomLogger logger;
    private final WebClient apiConnectClient;
    private final LoanApisProperties apisProperties;
    private final ITemplateOperations templateOperations;

    public LoanAdapter(final @Qualifier("ApiConnectClient") WebClient apiConnectClient,
                       final CustomLogger logger,
                       final LoanApisProperties apisProperties,
                       final ITemplateOperations templateOperations) {
        this.logger = logger;
        this.apiConnectClient = apiConnectClient;
        this.apisProperties = apisProperties;
        this.templateOperations = templateOperations;
    }

    @Override
    public Mono<Map<String, Object>> createApplication(String createApplicationTemplate, Map<String, Object> data) {
        return templateOperations.process(createApplicationTemplate, data, Map.class)
                .flatMap(this::sendRequestToApi)
                .doOnNext(response -> logger.info("The create application api was successful.", response));

    }

    public Mono<Map<String, Object>> sendRequestToApi(Map<String, Object> request) {
        return this.apiConnectClient.post()
                .uri(apisProperties.getCreateApplication())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorMap(throwTechnicalError(API_PROMOTER_ERROR));
    }

}
