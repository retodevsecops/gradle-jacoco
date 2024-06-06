package com.consubanco.usecase.file;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.file.message.FileBusinessMessage.FILES_NOT_FOUND;
import static com.consubanco.model.entities.file.message.FileBusinessMessage.OFFER_ID_IS_NULL;

@RequiredArgsConstructor
public class GetFilesByOfferUseCase {

    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final FileRepository fileRepository;

    public Flux<File> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .map(Process::getOfferId)
                .flatMap(this::checkOfferId)
                .map(FileConstants::offerDirectory)
                .flatMapMany(fileRepository::listByFolderWithUrls)
                .switchIfEmpty(ExceptionFactory.buildBusiness(FILES_NOT_FOUND));
    }

    private Mono<String> checkOfferId(String offerId) {
        return Mono.justOrEmpty(offerId)
                .switchIfEmpty(ExceptionFactory.buildBusiness(OFFER_ID_IS_NULL));
    }

}
