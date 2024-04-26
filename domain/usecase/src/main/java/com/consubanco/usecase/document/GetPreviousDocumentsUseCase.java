package com.consubanco.usecase.document;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetPreviousDocumentsUseCase {

    public Mono<Void> execute(){
        return Mono.empty();
    }

}
