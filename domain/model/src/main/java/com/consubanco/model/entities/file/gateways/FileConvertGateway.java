package com.consubanco.model.entities.file.gateways;

import reactor.core.publisher.Mono;

public interface FileConvertGateway {
    Mono<String> encodedFile(String fileUrl);
}
