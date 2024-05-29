package com.consubanco.usecase.agreement;

import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
public class LoadAgreementsUseCase {

    private final AgreementConfigRepository agreementConfigRepository;
    private final AgreementGateway agreementGateway;

    public Flux<Agreement> execute(){
        return agreementConfigRepository.getAgreementsConfig()
                .flatMapMany(Flux::fromIterable)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(AgreementConfigVO::getAgreementNumber)
                .flatMap(agreementGateway::findByNumber)
                .sequential();
    }

}
