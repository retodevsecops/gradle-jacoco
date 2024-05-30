package com.consubanco.usecase.document;

import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public class GetPayloadDataUseCase {

    private final PayloadDocumentGateway payloadGateway;

    public Mono<Map<String, Object>> execute(String processId) {
        return payloadGateway.getAllData(processId);

    }

}
