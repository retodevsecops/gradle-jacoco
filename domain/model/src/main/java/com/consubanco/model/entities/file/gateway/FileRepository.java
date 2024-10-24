package com.consubanco.model.entities.file.gateway;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.vo.FileStorageVO;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface FileRepository {
    Mono<File> save(File file);
    Mono<Void> delete(File file);

    Mono<File> saveWithSignedUrl(File file);

    Flux<File> listByFolderWithUrls(String folderPath);

    Flux<File> listByFolderWithoutUrls(String folderPath);

    Flux<FileStorageVO> listByFolder(String folderPath);

    Mono<File> getByNameWithSignedUrl(String name);

    Mono<File> getByNameWithoutSignedUrl(String name);

    Mono<File> getPayloadTemplate();

    Mono<File> getPayloadTemplateWithoutSignedUrl();

    Mono<File> loadPayloadTemplate();

    Mono<File> uploadPayloadTemplate(FileUploadVO fileUploadVO);

    Mono<File> uploadAgreementsConfigFile(File file);

    Double getMaxSizeOfFileInMBAllowed();

    Mono<File> getCreateApplicationTemplate();

    Mono<File> getCreateApplicationTemplateWithoutSignedUrl();

    Mono<File> uploadCreateApplicationTemplate(FileUploadVO fileUploadVO);

    Mono<File> loadCreateApplicationTemplate();

    Mono<Map> validateTemplate(String template, Map<String, Object> data);
}
