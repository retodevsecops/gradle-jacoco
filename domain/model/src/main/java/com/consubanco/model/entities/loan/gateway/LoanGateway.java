package com.consubanco.model.entities.loan.gateway;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface LoanGateway {
    Mono<Map<String, Object>> createApplication(String createApplicationTemplate, Map<String, Object> data);
}
