package com.consubanco.usecase.agreement;

import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
import com.consubanco.model.entities.agreement.gateway.PromoterGateway;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.List;

@RequiredArgsConstructor
public class LoadAgreementsUseCase {

    private final AgreementConfigRepository agreementConfigRepository;
    private final AgreementGateway agreementGateway;
    private final PromoterGateway promoterGateway;

    public Mono<Tuple2<List<String>, List<String>>> execute() {
        return agreementConfigRepository.getAgreementsConfig()
                .flatMap(configList -> Mono.zip(loadAgreements(configList), loadPromoters(configList)));
    }

    private Mono<List<String>> loadAgreements(List<AgreementConfigVO> agreementConfigVOList) {
        return Flux.fromIterable(agreementConfigVOList)
                .map(AgreementConfigVO::getAgreementNumber)
                .distinct()
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(agreementNumber -> agreementGateway.findByNumber(agreementNumber).thenReturn(agreementNumber))
                .sequential()
                .collectList();
    }

    private Mono<List<String>> loadPromoters(List<AgreementConfigVO> agreementConfigVOList) {
        return Flux.fromIterable(agreementConfigVOList)
                .map(AgreementConfigVO::getPromoterId)
                .distinct()
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(promoterId -> promoterGateway.findById(promoterId).thenReturn(promoterId))
                .sequential()
                .collectList();
    }

}
