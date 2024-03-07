package com.consubanco.model.agreement.gateways;

import com.consubanco.model.agreement.Agreement;
import reactor.core.publisher.Mono;

public interface AgreementRepository {
    Mono<Agreement> findByNumber(String agreementNumber);
}
