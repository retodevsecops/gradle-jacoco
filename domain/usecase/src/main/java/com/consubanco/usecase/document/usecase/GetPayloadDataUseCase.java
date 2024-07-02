package com.consubanco.usecase.document.usecase;

import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.agreement.GetAgreementConfigUseCase;
import com.consubanco.usecase.document.helper.BuildPayloadDataMapHelper;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public class GetPayloadDataUseCase {

    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final GetAgreementConfigUseCase agreementConfigUseCase;
    private final BuildPayloadDataMapHelper buildPayloadDataMapHelper;

    public Mono<Map<String, Object>> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(this::getData);

    }

    private Mono<Map<String, Object>> getData(Process process) {
        return agreementConfigUseCase.execute(process.getAgreementNumber())
                .flatMap(agreementConfigVO -> buildPayloadDataMapHelper.execute(process, agreementConfigVO));
    }

}
