package com.consubanco.model.entities.file.gateways;

import com.consubanco.model.entities.file.File;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FileRepository {
    Flux<File> bulkSave(List<File> files);
    Mono<File> save(File file);
    Flux<File> listByFolder(String offerId);
    Mono<File> getByName(String name);
    Mono<File> getPayloadTemplate();
}
