package com.consubanco.usecase.agreement;

import com.consubanco.model.commons.exception.BusinessException;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateways.AgreementRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.agreement.message.AgreementBusinessMessage.AGREEMENT_NOT_FOUND;

@RequiredArgsConstructor
public class AgreementUseCase {

    private final AgreementRepository agreementRepository;

    public Mono<Agreement> findByNumber(String agreementNumber) {
        return this.agreementRepository.findByNumber(agreementNumber)
                .switchIfEmpty(ExceptionFactory.buildBusiness(AGREEMENT_NOT_FOUND));
    }

}
