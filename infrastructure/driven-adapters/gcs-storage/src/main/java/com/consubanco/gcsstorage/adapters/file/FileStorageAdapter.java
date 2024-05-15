package com.consubanco.gcsstorage.adapters.file;

import com.consubanco.freemarker.ITemplateOperations;
import com.consubanco.gcsstorage.commons.ContentTypeResolver;
import com.consubanco.gcsstorage.commons.FileFactoryUtil;
import com.consubanco.gcsstorage.commons.FileUtil;
import com.consubanco.gcsstorage.config.GoogleStorageProperties;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URL;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.*;
import static com.google.cloud.storage.Storage.SignUrlOption.withV4Signature;
import static java.util.concurrent.TimeUnit.DAYS;

@Service
@RequiredArgsConstructor
public class FileStorageAdapter implements FileRepository {

    private final CustomLogger logger;
    private final Storage storage;
    private final GoogleStorageProperties properties;
    private final ITemplateOperations templateOperations;

    @Override
    public Mono<File> save(File file) {
        String contentType = ContentTypeResolver.getFromFileExtension(file.getExtension());
        Mono<BlobInfo> blob = FileUtil.buildBlob(properties.getBucketName(), file.fullPath(), contentType);
        Mono<byte[]> contentFile = FileUtil.base64ToBytes(file.getContent());
        return Mono.zip(blob, contentFile)
                .map(tuple -> storage.create(tuple.getT1(), tuple.getT2()))
                .map(blobInfo -> file.toBuilder()
                        .url(blobInfo.getSelfLink())
                        .size(FileUtil.getSize(blobInfo))
                        .build())
                .onErrorMap(throwTechnicalError(STORAGE_ERROR));
    }

    @Override
    public Flux<File> listByFolderWithUrls(String folderPath) {
        return listByPrefix(folderPath)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::buildFileEntityFromBlob)
                .sequential()
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(GET_FILE_ERROR));
    }

    @Override
    public Flux<File> listByFolderWithoutUrls(String folderPath) {
        return listByPrefix(folderPath)
                .map(FileFactoryUtil::buildFromBlob);
    }

    @Override
    public Mono<File> getByNameWithSignedUrl(String name) {
        BlobId blobId = BlobId.of(properties.getBucketName(), name);
        return Mono.justOrEmpty(storage.get(blobId))
                .onErrorMap(throwTechnicalError(FIND_FILE_ERROR))
                .flatMap(this::buildFileEntityFromBlob);
    }

    @Override
    public Mono<File> getByNameWithoutSignedUrl(String name) {
        BlobId blobId = BlobId.of(properties.getBucketName(), name);
        return Mono.justOrEmpty(storage.get(blobId))
                .onErrorMap(throwTechnicalError(FIND_FILE_ERROR))
                .map(FileFactoryUtil::buildFromBlob);
    }

    @Override
    @Cacheable("payloadTemplate")
    public Mono<File> getPayloadTemplate() {
        return getByNameWithSignedUrl(properties.getFilesPath().getPayloadTemplate())
                .doOnNext(file -> logger.info("Payload template was consulted.", file));
    }

    @Cacheable("payloadTemplate")
    public Mono<File> loadPayloadTemplate() {
        return getByNameWithoutSignedUrl(properties.getFilesPath().getPayloadTemplate())
                .switchIfEmpty(uploadLocalPayloadTemplate());
    }

    public Mono<File> uploadLocalPayloadTemplate() {
        return Mono.just(properties.payloadTemplatePath())
                .map(FileUtil::getFileName)
                .map(ClassPathResource::new)
                .flatMap(FileUtil::buildFileUploadVOFromResource)
                .onErrorMap(throwTechnicalError(LOCAL_TEMPLATE_ERROR))
                .doOnNext(e -> logger.info("Payload template was get from local source."))
                .flatMap(this::uploadPayloadTemplate);
    }

    @Override
    @CacheEvict(cacheNames = "payloadTemplate", allEntries = true)
    public Mono<File> uploadPayloadTemplate(FileUploadVO fileUploadVO) {
        return uploadTemplate(fileUploadVO, properties.payloadTemplatePath());
    }

    @Override
    @CacheEvict(cacheNames = "AgreementConfig", allEntries = true)
    public Mono<File> uploadAgreementsConfigFile(File file) {
        return Mono.just(properties.getFilesPath().getAgreementsConfig())
                .map(path -> file.toBuilder()
                        .name(FileUtil.getFileName(path))
                        .directoryPath(FileUtil.getDirectory(path))
                        .build())
                .flatMap(this::save);
    }

    @Override
    public Double getMaxSizeOfFileInMBAllowed() {
        return properties.getMaxFileSizeMB();
    }


    @Override
    @Cacheable("createApplicationTemplate")
    public Mono<File> getCreateApplicationTemplate() {
        return getByNameWithSignedUrl(properties.getFilesPath().getCreateApplicationTemplate())
                .doOnNext(file -> logger.info("Create application template was consulted in to storage.", file));
    }

    @Override
    @CacheEvict(cacheNames = "createApplicationTemplate", allEntries = true)
    public Mono<File> uploadCreateApplicationTemplate(FileUploadVO fileUploadVO) {
        return uploadTemplate(fileUploadVO, properties.getFilesPath().getCreateApplicationTemplate());
    }

    @Override
    @Cacheable("createApplicationTemplate")
    public Mono<File> loadCreateApplicationTemplate() {
        return getByNameWithoutSignedUrl(properties.getFilesPath().getCreateApplicationTemplate())
                .switchIfEmpty(uploadLocalCreateApplicationTemplate());
    }

    private Mono<File> uploadLocalCreateApplicationTemplate() {
        return Mono.just(properties.getFilesPath().getCreateApplicationTemplate())
                .map(FileUtil::getFileName)
                .map(ClassPathResource::new)
                .flatMap(FileUtil::buildFileUploadVOFromResource)
                .flatMap(this::uploadCreateApplicationTemplate)
                .doOnNext(e -> logger.info("The create application template was get from local source."))
                .onErrorMap(throwTechnicalError(LOCAL_TEMPLATE_ERROR));
    }

    private Mono<File> uploadTemplate(FileUploadVO fileUploadVO, String path) {
        return Mono.just(fileUploadVO.getContent())
                .map(FileUtil::decodeBase64)
                .filter(templateOperations::validate)
                .map(isValid -> File.builder()
                        .name(FileUtil.getFileName(path))
                        .directoryPath(FileUtil.getDirectory(path))
                        .content(fileUploadVO.getContent())
                        .extension(fileUploadVO.getExtension())
                        .build())
                .flatMap(this::save);
    }

    private Mono<File> buildFileEntityFromBlob(Blob blob) {
        return signUrl(blob)
                .map(signUrl -> FileFactoryUtil.buildFromBlobWithUrl(blob, signUrl));
    }

    private Flux<Blob> listByPrefix(String folderPath) {
        return Mono.just(folderPath)
                .map(Storage.BlobListOption::prefix)
                .map(option -> storage.list(properties.getBucketName(), option))
                .flatMapIterable(Page::iterateAll)
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(GET_FILE_ERROR));
    }

    private Mono<String> signUrl(Blob blob) {
        return FileUtil.buildBlob(properties.getBucketName(), blob.getName(), blob.getContentType())
                .map(blobInfo -> storage.signUrl(blobInfo, properties.getSignUrlDays(), DAYS, withV4Signature()))
                .map(URL::toString)
                .onErrorMap(throwTechnicalError(SIGN_URL_ERROR));
    }

}
