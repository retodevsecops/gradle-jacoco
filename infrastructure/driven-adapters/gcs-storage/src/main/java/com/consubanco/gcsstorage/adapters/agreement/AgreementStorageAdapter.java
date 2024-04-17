package com.consubanco.gcsstorage.adapters.agreement;

import com.consubanco.gcsstorage.commons.FileFactoryUtil;
import com.consubanco.gcsstorage.config.GoogleStorageProperties;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.FIND_FILE_ERROR;

@Service
@RequiredArgsConstructor
public class AgreementStorageAdapter implements AgreementConfigRepository {

    private final CustomLogger logger;
    private final Storage storage;
    private final GoogleStorageProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public Flux<AgreementConfigVO> getAllConfig() {
        BlobId blobId = BlobId.of(properties.getBucketName(), properties.getFilesPath().getAgreementsConfig());
        return Mono.justOrEmpty(storage.get(blobId))
                .map(Blob::getContent)
                .map(this::deserializeAgreementConfigList)
                .flatMapMany(Flux::fromIterable)
                .onErrorMap(throwTechnicalError(FIND_FILE_ERROR));
    }

    private List<AgreementConfigVO> deserializeAgreementConfigList(byte[] content) {
        try {
            return objectMapper.readValue(content, new TypeReference<List<AgreementConfigVO>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing agreement config JSON", e);
        }
    }

}
