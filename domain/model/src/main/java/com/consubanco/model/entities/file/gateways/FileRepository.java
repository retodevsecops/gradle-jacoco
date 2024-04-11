package com.consubanco.model.entities.file.gateways;

import com.consubanco.model.entities.file.File;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileRepository {
    Mono<File> save(File file);
    Flux<File> listByFolder(String offerId);
    Mono<File> getByName(String name);
    Mono<File> getPayloadTemplate();
    Mono<String> getLocalPayloadTemplate();
    Mono<File> uploadPayloadTemplate(String contentFile);
}
