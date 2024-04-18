package com.consubanco.usecase.file;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.file.message.FileBusinessMessage.*;

@RequiredArgsConstructor
public class FileUseCase {

    private final FileRepository fileRepository;

    public Flux<File> getFilesByOffer(String offerId) {
        return checkOfferId(offerId)
                .map(FileConstants::offerDirectory)
                .flatMapMany(fileRepository::listByFolder)
                .switchIfEmpty(ExceptionFactory.buildBusiness(FILES_NOT_FOUND));
    }

    public Mono<File> loadPayloadTemplate() {
        return fileRepository.getPayloadTemplate()
                .switchIfEmpty(fileRepository.getLocalPayloadTemplate()
                        .switchIfEmpty(ExceptionFactory.buildBusiness(PAYLOAD_TEMPLATE_NOT_FOUND))
                        .flatMap(this::uploadPayloadTemplate));
    }

    public Mono<File> uploadPayloadTemplate(String contentFile) {
        return fileRepository.uploadPayloadTemplate(contentFile)
                .switchIfEmpty(ExceptionFactory.buildBusiness(PAYLOAD_TEMPLATE_INCORRECT))
                .flatMap(file -> fileRepository.getPayloadTemplate());
    }

    public Mono<File> uploadAgreementsConfig(String contentFile) {
        return fileRepository.uploadAgreementsConfigFile(contentFile);
    }

    private Mono<String> checkOfferId(String offerId) {
        return Mono.justOrEmpty(offerId)
                .switchIfEmpty(ExceptionFactory.buildBusiness(OFFER_ID_IS_NULL));
    }

}
