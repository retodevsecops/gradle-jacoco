package com.consubanco.usecase.agreement;

import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class LoadAgreementsUseCase {

    private final AgreementConfigRepository agreementConfigRepository;
    private final AgreementGateway agreementGateway;

    public Flux<Agreement> execute(){
        return agreementConfigRepository.getAllConfig()
                .flatMap(agreementConfigVO -> agreementGateway.findByNumber(agreementConfigVO.getAgreementNumber()));
    }

}
