package com.consubanco.usecase.document.usecase;

import com.consubanco.model.entities.agreement.Agreement;
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
                .flatMap(agreement -> buildDocuments(process, agreement));
    }

    private Mono<Void> buildDocuments(Process process, Agreement agreement) {
        return buildAgreementDocumentsUseCase.execute(process, agreement)
                .then(buildCompoundDocumentsUseCase.execute(process, agreement));
    }

}
