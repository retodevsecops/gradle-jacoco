package com.consubanco.gcsstorage.adapters.file;

import com.consubanco.freemarker.ITemplateOperations;
import com.consubanco.gcsstorage.commons.FileFactoryUtil;
import com.consubanco.gcsstorage.commons.FileUtil;
import com.consubanco.gcsstorage.config.GoogleStorageProperties;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.*;

@Service
@RequiredArgsConstructor
public class FileStorageAdapter implements FileRepository {

    private final CustomLogger logger;
    private final Storage storage;
    private final GoogleStorageProperties properties;
    private final ITemplateOperations templateOperations;

    @Override
    public Mono<File> save(File file) {
        Mono<BlobInfo> blob = FileUtil.buildBlob(properties.getBucketName(), file.fullPath());
        Mono<byte[]> contentFile = FileUtil.base64ToBytes(file.getContent());
        return Mono.zip(blob, contentFile)
                .map(tuple -> storage.create(tuple.getT1(), tuple.getT2()))
                .map(blobInfo -> file.toBuilder()
                        .url(properties.getPublicUrl(blobInfo.getName()))
                        .size(FileUtil.getSize(blobInfo))
                        .build())
                .onErrorMap(throwTechnicalError(STORAGE_ERROR));
    }

    @Override
    public Flux<File> listByFolder(String folderPath) {
        return Mono.just(Storage.BlobListOption.prefix(folderPath))
                .map(option -> storage.list(properties.getBucketName(), option))
                .flatMapIterable(Page::iterateAll)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::buildFileEntityFromBlob)
                .sequential()
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(GET_FILE_ERROR));
    }

    @Override
    public Mono<File> getByName(String name) {
        BlobId blobId = BlobId.of(properties.getBucketName(), name);
        return Mono.justOrEmpty(storage.get(blobId))
                .map(FileFactoryUtil::buildFromBlob)
                .onErrorMap(throwTechnicalError(FIND_FILE_ERROR));
    }

    @Override
    @Cacheable("payloadTemplate")
    public Mono<File> getPayloadTemplate() {
        return getByName(properties.getFilesPath().getPayloadTemplate())
                .doOnNext(file -> logger.info("Payload template was consulted.", file));
    }

    @Override
    public Mono<String> getLocalPayloadTemplate() {
        return Mono.just(properties.payloadTemplatePath())
                .map(FileUtil::getFileName)
                .map(ClassPathResource::new)
                .map(resource -> {
                    try {
                        return FileCopyUtils.copyToByteArray(resource.getInputStream());
                    } catch (IOException exception) {
                        throw ExceptionFactory.buildTechnical(exception, LOCAL_TEMPLATE_ERROR);
                    }
                })
                .map(Base64::encodeBase64String)
                .doOnTerminate(() -> logger.info("Payload template was get from local source."));
    }

    @Override
    @CacheEvict(cacheNames = "payloadTemplate", allEntries = true)
    public Mono<File> uploadPayloadTemplate(String contentFile) {
        return Mono.just(contentFile)
                .map(FileUtil::decodeBase64)
                .filter(templateOperations::validate)
                .map(isValid -> File.builder()
                        .name(FileUtil.getFileName(properties.payloadTemplatePath()))
                        .directoryPath(FileUtil.getDirectory(properties.payloadTemplatePath()))
                        .content(contentFile)
                        .build())
                .flatMap(this::save);
    }

    private Mono<File> buildFileEntityFromBlob(Blob blob) {
        return signUrl(blob.getName())
                .map(signUrl -> FileFactoryUtil.buildFromBlobWithUrl(blob, signUrl));
    }

    private Mono<String> signUrl(String blobName) {
        return FileUtil.buildBlob(properties.getBucketName(), blobName)
                .map(blobInfo -> storage.signUrl(blobInfo, properties.getSignUrlDays(), TimeUnit.DAYS))
                .map(URL::toString)
                .onErrorMap(throwTechnicalError(SIGN_URL_ERROR));
    }

}
