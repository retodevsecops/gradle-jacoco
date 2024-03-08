package com.consubanco.model.entities.agreement.gateways;

import com.consubanco.model.entities.agreement.Agreement;
import reactor.core.publisher.Mono;
public interface AgreementRepository {
    Mono<Agreement> findByNumber(String agreementNumber);
}
