package com.consubanco.api.services.file;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.file.dto.BuildCNCALettersRequestDTO;
import com.consubanco.usecase.file.FileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FileHandler {

    private final FileUseCase fileUseCase;

    public Mono<ServerResponse> buildCNCALetters(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BuildCNCALettersRequestDTO.class)
                .map(BuildCNCALettersRequestDTO::getOffer)
                .flatMapMany(offerDTO -> fileUseCase.buildCNCALetters(offerDTO.getId(), offerDTO.getLoansId()))
                .collectList()
                .flatMap(HttpResponseUtil::Ok);
    }

}
