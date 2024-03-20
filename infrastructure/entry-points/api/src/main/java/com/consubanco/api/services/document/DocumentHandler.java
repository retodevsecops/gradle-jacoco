package com.consubanco.api.services.document;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.document.dto.GenerateDocumentRequestDTO;
import com.consubanco.api.services.document.dto.GenerateDocumentResponseDTO;
import com.consubanco.usecase.document.DocumentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DocumentHandler {

    private final DocumentUseCase documentUseCase;

    public Mono<ServerResponse> generateDocument(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(GenerateDocumentRequestDTO.class)
                .flatMap(request -> documentUseCase.generate(request.documentsToEntity(), request.getPayload()))
                .map(GenerateDocumentResponseDTO::new)
                .flatMap(HttpResponseUtil::Ok);
    }

}
