package com.consubanco.model.entities.agreement.gateway;

import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AgreementConfigRepository {
    Mono<List<AgreementConfigVO>> getAgreementsConfig();
}
