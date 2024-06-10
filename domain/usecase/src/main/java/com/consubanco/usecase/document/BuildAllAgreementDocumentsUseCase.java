package com.consubanco.usecase.document;

import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.agreement.AgreementUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BuildAllAgreementDocumentsUseCase {

    private final AgreementUseCase agreementUseCase;
    private final BuildAgreementDocumentsUseCase buildAgreementDocumentsUseCase;
    private final BuildCompoundDocumentsUseCase buildCompoundDocumentsUseCase;

    public Mono<Void> execute(Process process) {
        return agreementUseCase.findByNumber(process.getAgreementNumber())
                .flatMapMany(agreement -> buildAgreementDocumentsUseCase.execute(process, agreement.getDocuments()))
                .collectList()
                .flatMap(docsGenerated -> buildCompoundDocumentsUseCase.execute(process, docsGenerated));
    }

}
