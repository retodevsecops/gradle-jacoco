package com.consubanco.usecase.document;

import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.agreement.GetAgreementConfigUseCase;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public class GetPayloadDataUseCase {

    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final PayloadDocumentGateway payloadGateway;
    private final GetAgreementConfigUseCase agreementConfigUseCase;

    public Mono<Map<String, Object>> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(this::getData);

    }

    private Mono<Map<String, Object>> getData(Process process) {
        return agreementConfigUseCase.execute(process.getAgreementNumber())
                .flatMap(agreementConfigVO -> payloadGateway.getAllData(process.getId(), agreementConfigVO));
    }

}
