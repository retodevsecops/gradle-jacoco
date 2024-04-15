package com.consubanco.model.entities.file.gateway;

import reactor.core.publisher.Mono;

public interface FileConvertGateway {
    Mono<String> encodedFile(String fileUrl);
}
