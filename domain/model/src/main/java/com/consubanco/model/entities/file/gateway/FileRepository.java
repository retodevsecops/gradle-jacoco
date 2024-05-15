package com.consubanco.model.entities.file.gateway;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileRepository {
    Mono<File> save(File file);
    Flux<File> listByFolderWithUrls(String folderPath);
    Flux<File> listByFolderWithoutUrls(String folderPath);
    Mono<File> getByNameWithSignedUrl(String name);
    Mono<File> getByNameWithoutSignedUrl(String name);
    Mono<File> getPayloadTemplate();
    Mono<File> loadPayloadTemplate();
    Mono<File> uploadPayloadTemplate(FileUploadVO fileUploadVO);
    Mono<File> uploadAgreementsConfigFile(File file);
    Double getMaxSizeOfFileInMBAllowed();
    Mono<File> getCreateApplicationTemplate();
    Mono<File> uploadCreateApplicationTemplate(FileUploadVO fileUploadVO);
    Mono<File> loadCreateApplicationTemplate();
}
