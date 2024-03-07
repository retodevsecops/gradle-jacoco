package com.consubanco.usecase.agreement;

import com.consubanco.model.agreement.Agreement;
import com.consubanco.model.agreement.gateways.AgreementRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AgreementUseCase {

    private final AgreementRepository agreementRepository;

    public Mono<Agreement> findByNumber(String agreementNumber) {
        return  this.agreementRepository.findByNumber(agreementNumber);
    }

}
