package com.consubanco.usecase.file;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateways.FileRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.file.message.FileBusinessMessage.OFFER_ID_IS_NULL;
import static com.consubanco.model.entities.file.message.FileBusinessMessage.PAYLOAD_TEMPLATE_NOT_FOUND;

@RequiredArgsConstructor
public class FileUseCase {

    private final FileRepository fileRepository;

    public Flux<File> getFilesByOffer(String offerId) {
        return checkOfferId(offerId)
                .map(FileConstants::offerDirectory)
                .flatMapMany(fileRepository::listByFolder);
    }

    public Mono<File> getPayloadTemplate() {
        return fileRepository.getPayloadTemplate()
                .switchIfEmpty(ExceptionFactory.buildBusiness(PAYLOAD_TEMPLATE_NOT_FOUND));
    }

    private Mono<String> checkOfferId(String offerId) {
        return Mono.justOrEmpty(offerId)
                .switchIfEmpty(ExceptionFactory.buildBusiness(OFFER_ID_IS_NULL));
    }

}
