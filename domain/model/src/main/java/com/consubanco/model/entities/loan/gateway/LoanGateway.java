package com.consubanco.model.entities.loan.gateway;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface LoanGateway {
    Mono<Map<String, Object>> buildApplicationData(String createApplicationTemplate, Map<String, Object> data);
    Mono<Map<String, Object>> createApplication(Map<String, Object> applicationData);
}
