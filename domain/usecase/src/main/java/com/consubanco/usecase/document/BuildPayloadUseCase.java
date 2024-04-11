package com.consubanco.usecase.document;

import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateways.FileRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.Map;

@RequiredArgsConstructor
public class BuildPayloadUseCase {

    private final FileRepository fileRepository;
    private final PayloadDocumentGateway payloadGateway;

    public Mono<Map<String, Object>> execute() {
        return Mono.zip(getPayloadTemplate(), payloadGateway.getAllData())
                .flatMap(TupleUtils.function(payloadGateway::buildPayload));
    }

    private Mono<String> getPayloadTemplate() {
        return fileRepository.getPayloadTemplate()
                .map(File::getContent);
    }

}
