package com.consubanco.gcsstorage.adapters.file;

import com.consubanco.gcsstorage.commons.FileFactoryUtil;
import com.consubanco.gcsstorage.commons.FileUtil;
import com.consubanco.gcsstorage.config.GoogleStorageProperties;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateways.FileRepository;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.*;

@Service
@RequiredArgsConstructor
public class FileStorageAdapter implements FileRepository {

    private final CustomLogger logger;
    private final Storage storage;
    private final GoogleStorageProperties properties;

    @Override
    public Flux<File> bulkSave(List<File> files) {
        return Flux.fromIterable(files)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::save)
                .sequential();
    }

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
                .doOnNext(file -> logger.info("The payload template was consulted.", file));
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
