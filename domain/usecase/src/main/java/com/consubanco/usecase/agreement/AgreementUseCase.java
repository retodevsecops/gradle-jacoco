package com.consubanco.usecase.agreement;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.agreement.message.AgreementBusinessMessage.AGREEMENT_NOT_FOUND;

@RequiredArgsConstructor
public class AgreementUseCase {

    private final AgreementGateway agreementGateway;

    public Mono<Agreement> findByNumber(String agreementNumber) {
        return this.agreementGateway.findByNumber(agreementNumber)
                .switchIfEmpty(ExceptionFactory.buildBusiness(AGREEMENT_NOT_FOUND));
    }

    public Flux<Agreement.Document> getAttachments(String agreementNumber) {
        return this.agreementGateway.findByNumber(agreementNumber)
                .map(Agreement::getAttachments)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(ExceptionFactory.buildBusiness(AGREEMENT_NOT_FOUND));
    }

}
