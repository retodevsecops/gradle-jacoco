package com.consubanco.model.entities.file.gateway;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileRepository {
    Mono<File> save(File file);
    Flux<File> listByFolder(String path);
    Mono<File> getByName(String name);
    Mono<File> getPayloadTemplate();
    Mono<FileUploadVO> getLocalPayloadTemplate();
    Mono<File> uploadPayloadTemplate(FileUploadVO fileUploadVO);
    Mono<File> uploadAgreementsConfigFile(File file);
}
