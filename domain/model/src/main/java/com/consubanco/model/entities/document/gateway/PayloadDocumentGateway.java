package com.consubanco.model.entities.document.gateway;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface PayloadDocumentGateway {

    Mono<Map<String, Object>> getAllData();
    Mono<Map<String, Object>> buildPayload(String template, Map<String, Object> data);

}
