package com.consubanco.usecase.agreement;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.agreement.message.AgreementBusinessMessage.AGREEMENT_CONFIG_NOT_FOUND;
import static com.consubanco.model.entities.agreement.message.AgreementMessage.configNotFound;

@RequiredArgsConstructor
public class GetAgreementConfigUseCase {

    private final AgreementConfigRepository agreementConfigRepository;

    public Mono<AgreementConfigVO> execute(String agreementNumber) {
        return agreementConfigRepository.getConfigByAgreement(agreementNumber)
                .switchIfEmpty(ExceptionFactory.monoBusiness(AGREEMENT_CONFIG_NOT_FOUND, configNotFound(agreementNumber)));
    }

}
