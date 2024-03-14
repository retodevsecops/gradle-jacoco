package com.consubanco.model.entities.file.gateways;

import reactor.core.publisher.Mono;

public interface FileGateway {
    Mono<String> getContentCNCALetter(String loanId);
}
