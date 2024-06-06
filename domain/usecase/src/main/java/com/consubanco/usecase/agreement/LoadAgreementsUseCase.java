package com.consubanco.usecase.agreement;

import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
import com.consubanco.model.entities.agreement.gateway.PromoterGateway;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class LoadAgreementsUseCase {

    private final AgreementConfigRepository agreementConfigRepository;
    private final AgreementGateway agreementGateway;
    private final PromoterGateway promoterGateway;

    public Flux<Tuple2<Agreement, Map<String, Object>>> execute() {
        return agreementConfigRepository.getAgreementsConfig()
                .flatMapMany(configList -> Flux.zip(loadAgreements(configList), loadPromoters(configList)));
    }

    private Flux<Agreement> loadAgreements(List<AgreementConfigVO> agreementConfigVOList) {
        return Flux.fromIterable(agreementConfigVOList)
                .map(AgreementConfigVO::getAgreementNumber)
                .distinct()
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(agreementGateway::findByNumber)
                .sequential();
    }

    private Flux<Map<String, Object>> loadPromoters(List<AgreementConfigVO> agreementConfigVOList) {
        return Flux.fromIterable(agreementConfigVOList)
                .map(AgreementConfigVO::getPromoterId)
                .distinct()
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(promoterGateway::findById)
                .sequential();
    }

}
