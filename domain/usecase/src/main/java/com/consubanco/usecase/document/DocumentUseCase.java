package com.consubanco.usecase.document;

import com.consubanco.model.entities.document.Document;
import com.consubanco.model.entities.document.gateway.DocumentGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DocumentUseCase {

    private final DocumentGateway documentGateway;

    public Mono<String> generate(List<Document> documents, Map<String, Object> payload) {
        return documentGateway.generate(documents, payload);
    }

}
