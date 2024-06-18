package com.consubanco.model.entities.email.gateway;

import reactor.core.publisher.Mono;

public interface EmailGateway {
    Mono<Boolean> sendEmail(String email, String bp, String base64File);
}
