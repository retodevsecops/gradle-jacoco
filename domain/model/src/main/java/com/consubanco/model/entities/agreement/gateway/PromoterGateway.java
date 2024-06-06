package com.consubanco.model.entities.agreement.gateway;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface PromoterGateway {
    Mono<Map<String, Object>> findById(String promoterId);
}
