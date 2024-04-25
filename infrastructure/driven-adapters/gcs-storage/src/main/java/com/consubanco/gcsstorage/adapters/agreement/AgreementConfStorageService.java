package com.consubanco.gcsstorage.adapters.agreement;

import com.consubanco.gcsstorage.commons.ContentTypeResolver;
import com.consubanco.gcsstorage.commons.FileUtil;
import com.consubanco.gcsstorage.config.GoogleStorageProperties;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.agreement.message.AgreementTechnicalMessage.*;

@Service
@RequiredArgsConstructor
public class AgreementConfStorageService {

    private final CustomLogger logger;
    private final Storage storage;
    private final GoogleStorageProperties properties;
    private final ObjectMapper objectMapper;

    @Cacheable("AgreementConfig")
    public Mono<List<AgreementConfigVO>> getAgreementsConfig() {
        Mono<byte[]> localAgreementsConfig = uploadLocalAgreementsConfig();
        return getAgreementsConfigFromStorage()
                .switchIfEmpty(localAgreementsConfig)
                .map(this::deserializeAgreementConfigList);
    }

    private Mono<byte[]> uploadLocalAgreementsConfig() {
        return getAgreementsConfigFromLocal()
                .flatMap(this::uploadToStorage)
                .map(Blob::getContent);
    }

    private Mono<byte[]> getAgreementsConfigFromLocal() {
        return Mono.just(properties.getFilesPath().getAgreementsConfig())
                .map(FileUtil::getFileName)
                .map(ClassPathResource::new)
                .map(FileUtil::getContentFromResource)
                .doOnNext(e -> logger.info("The Agreements configuration was get from local source."))
                .onErrorMap(throwTechnicalError(FAIL_GET_CONFIG_LOCAL));
    }

    private Mono<Blob> uploadToStorage(byte[] contentFile) {
        String contentType = ContentTypeResolver.getFromFileExtension(FileExtensions.JSON);
        return FileUtil.buildBlob(properties.getBucketName(), properties.agreementsConfigPath(), contentType)
                .map(blobInfo -> storage.create(blobInfo, contentFile))
                .onErrorMap(throwTechnicalError(FAIL_UPLOAD_CONFIG))
                .doOnTerminate(() -> logger.info("The local agreements configuration file was upload to google storage."));
    }

    private Mono<byte[]> getAgreementsConfigFromStorage() {
        BlobId blobId = BlobId.of(properties.getBucketName(), properties.getFilesPath().getAgreementsConfig());
        return Mono.justOrEmpty(storage.get(blobId))
                .map(Blob::getContent)
                .doOnNext(e -> logger.info("The Agreements configuration was get from google storage source."))
                .onErrorMap(throwTechnicalError(FAIL_GET_CONFIG_STORAGE));
    }

    private List<AgreementConfigVO> deserializeAgreementConfigList(byte[] content) {
        try {
            return objectMapper.readValue(content, new TypeReference<List<AgreementConfigVO>>() {});
        } catch (IOException exception) {
            throw buildTechnical(exception, STRUCTURE_INVALID);
        }
    }

}
