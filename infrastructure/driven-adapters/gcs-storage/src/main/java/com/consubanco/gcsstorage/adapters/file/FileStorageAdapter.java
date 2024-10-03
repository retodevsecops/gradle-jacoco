package com.consubanco.gcsstorage.adapters.file;

import com.consubanco.freemarker.ITemplateOperations;
import com.consubanco.gcsstorage.commons.FileFactoryUtil;
import com.consubanco.gcsstorage.commons.FileStorageUtil;
import com.consubanco.gcsstorage.config.GoogleStorageProperties;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.model.entities.file.vo.FileWithStorageRouteVO;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.*;
import static com.google.cloud.storage.Storage.SignUrlOption.withV4Signature;
import static java.util.concurrent.TimeUnit.DAYS;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FileStorageAdapter implements FileRepository {

    private final CustomLogger logger;
    private final Storage storage;
    private final GoogleStorageProperties properties;
    private final ITemplateOperations templateOperations;

    @Override
    public Mono<File> save(File file) {
        return saveInStorage(file)
                .map(blobInfo -> FileFactoryUtil.completeFileFromBlob(file, blobInfo));
    }

    @Override
    public Mono<File> saveWithSignedUrl(File file) {
        return saveInStorage(file)
                .flatMap(this::buildFileEntityFromBlob);
    }

    private Mono<Blob> saveInStorage(File file) {
        Mono<BlobInfo> blob = FileStorageUtil.buildBlobFromFile(file, properties.getBucketName());
        Mono<byte[]> contentFile = FileStorageUtil.base64ToBytes(file.getContent());
        return Mono.zip(blob, contentFile)
                .map(tuple -> storage.create(tuple.getT1(), tuple.getT2()))
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
    public Flux<FileWithStorageRouteVO> listByFolder(String folderPath) {
        return listByPrefix(folderPath)
                .map(FileFactoryUtil::buildFileWithStorageRouteVO);
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
                .filter(Objects::nonNull)
                .map(FileFactoryUtil::buildFromBlob)
                .onErrorResume(StorageException.class, e -> e.getCode() == NOT_FOUND.value() ? Mono.empty() : Mono.error(e))
                .onErrorMap(throwTechnicalError(FIND_FILE_ERROR));
    }

    @Override
    @Cacheable(cacheNames = "templates", key = "'payload'")
    public Mono<File> getPayloadTemplate() {
        return getByNameWithSignedUrl(properties.getFilesPath().getPayloadTemplate())
                .doOnNext(file -> logger.info("Payload template was consulted.", file));
    }

    @Override
    @Cacheable(cacheNames = "templates", key = "'payload'")
    public Mono<File> getPayloadTemplateWithoutSignedUrl() {
        return getByNameWithoutSignedUrl(properties.getFilesPath().getPayloadTemplate())
                .doOnNext(file -> logger.info("Payload template was consulted.", file));
    }

    @Override
    public Mono<File> loadPayloadTemplate() {
        return getByNameWithoutSignedUrl(properties.getFilesPath().getPayloadTemplate())
                .switchIfEmpty(uploadLocalPayloadTemplate());
    }

    public Mono<File> uploadLocalPayloadTemplate() {
        return Mono.just(properties.payloadTemplatePath())
                .map(FileStorageUtil::getFileNameWithExtension)
                .map(ClassPathResource::new)
                .flatMap(FileStorageUtil::buildFileUploadVOFromResource)
                .onErrorMap(throwTechnicalError(LOCAL_TEMPLATE_ERROR))
                .doOnNext(e -> logger.info("Payload template was get from local source."))
                .flatMap(this::uploadPayloadTemplate);
    }

    @Override
    @CacheEvict(cacheNames = "templates", allEntries = true)
    public Mono<File> uploadPayloadTemplate(FileUploadVO fileUploadVO) {
        return uploadTemplate(fileUploadVO, properties.payloadTemplatePath());
    }

    @Override
    @CacheEvict(cacheNames = "agreements-configuration", allEntries = true)
    public Mono<File> uploadAgreementsConfigFile(File file) {
        return Mono.just(properties.getFilesPath().getAgreementsConfig())
                .map(path -> file.toBuilder()
                        .name(FileStorageUtil.getFileName(path))
                        .directoryPath(FileStorageUtil.getDirectory(path))
                        .build())
                .flatMap(this::saveWithSignedUrl);
    }

    @Override
    public Double getMaxSizeOfFileInMBAllowed() {
        return properties.getMaxFileSizeMB();
    }

    @Override
    @Cacheable(cacheNames = "templates", key = "'create-application'")
    public Mono<File> getCreateApplicationTemplate() {
        return getByNameWithSignedUrl(properties.getFilesPath().getCreateApplicationTemplate())
                .doOnNext(file -> logger.info("Create application template was consulted in to storage.", file));
    }

    @Override
    @Cacheable(cacheNames = "templates", key = "'create-application'")
    public Mono<File> getCreateApplicationTemplateWithoutSignedUrl() {
        return getByNameWithoutSignedUrl(properties.getFilesPath().getCreateApplicationTemplate())
                .doOnNext(file -> logger.info("Create application template was consulted in to storage.", file));
    }

    @Override
    @CacheEvict(cacheNames = "templates", allEntries = true)
    public Mono<File> uploadCreateApplicationTemplate(FileUploadVO fileUploadVO) {
        return uploadTemplate(fileUploadVO, properties.getFilesPath().getCreateApplicationTemplate());
    }

    @Override
    public Mono<File> loadCreateApplicationTemplate() {
        return getByNameWithoutSignedUrl(properties.getFilesPath().getCreateApplicationTemplate())
                .switchIfEmpty(uploadLocalCreateApplicationTemplate());
    }

    @Override
    public Mono<Map> validateTemplate(String template, Map<String, Object> data) {
        return this.getTemplate(template)
                .flatMap(templateAsBase64 -> templateOperations.process(templateAsBase64, data, Map.class));
    }

    private Mono<File> uploadLocalCreateApplicationTemplate() {
        return Mono.just(properties.getFilesPath().getCreateApplicationTemplate())
                .map(FileStorageUtil::getFileNameWithExtension)
                .map(ClassPathResource::new)
                .flatMap(FileStorageUtil::buildFileUploadVOFromResource)
                .flatMap(this::uploadCreateApplicationTemplate)
                .doOnNext(e -> logger.info("The create application template was get from local source."))
                .onErrorMap(throwTechnicalError(LOCAL_TEMPLATE_ERROR));
    }

    private Mono<File> uploadTemplate(FileUploadVO fileUploadVO, String path) {
        return Mono.just(fileUploadVO.getContent())
                .map(FileStorageUtil::decodeBase64)
                .filter(templateOperations::validate)
                .map(isValid -> File.builder()
                        .name(FileStorageUtil.getFileName(path))
                        .directoryPath(FileStorageUtil.getDirectory(path))
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
                .doOnError(error -> logger.error("Error when consulting the files in folder " + folderPath, error))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(GET_FILE_ERROR));
    }

    private Mono<String> signUrl(Blob blob) {
        return FileStorageUtil.buildBlob(properties.getBucketName(), blob.getName(), blob.getContentType())
                .map(blobInfo -> storage.signUrl(blobInfo, properties.getSignUrlDays(), DAYS, withV4Signature()))
                .map(URL::toString)
                .onErrorMap(throwTechnicalError(SIGN_URL_ERROR));
    }

    private Mono<String> getTemplate(String template) {
        if (template.equalsIgnoreCase("payload"))
            return getPayloadTemplateWithoutSignedUrl().map(File::getContent);
        if (template.equalsIgnoreCase("create-application"))
            return getCreateApplicationTemplateWithoutSignedUrl().map(File::getContent);
        String templateAsBase64 = Base64.getEncoder().encodeToString(template.getBytes(StandardCharsets.UTF_8));
        return Mono.just(templateAsBase64);
    }

}
