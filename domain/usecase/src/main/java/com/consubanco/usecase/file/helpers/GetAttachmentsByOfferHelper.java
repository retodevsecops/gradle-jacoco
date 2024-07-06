package com.consubanco.usecase.file.helpers;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.file.message.FileBusinessMessage.ATTACHMENTS_NOT_FOUND;
import static com.consubanco.model.entities.file.message.FileBusinessMessage.OFFER_ID_IS_NULL;
import static com.consubanco.model.entities.file.message.FileMessage.attachmentsNotFound;

@RequiredArgsConstructor
public class GetAttachmentsByOfferHelper {

    private final FileRepository fileRepository;

    public Flux<File> execute(String offerId) {
        return checkOfferId(offerId)
                .map(FileConstants::attachmentsDirectory)
                .flatMapMany(fileRepository::listByFolderWithoutUrls)
                .switchIfEmpty(ExceptionFactory.monoBusiness(ATTACHMENTS_NOT_FOUND, attachmentsNotFound(offerId)));
    }

    private Mono<String> checkOfferId(String offerId) {
        return Mono.justOrEmpty(offerId)
                .switchIfEmpty(ExceptionFactory.buildBusiness(OFFER_ID_IS_NULL));
    }

}
