package com.consubanco.model.entities.loan.gateway;

import com.consubanco.model.entities.loan.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {
    Mono<LoanApplication> saveApplication(LoanApplication loanApplication);

    Mono<Void> updateOfferAndEmailStatus(Integer applicationId, String offerStatus, String emailStatus);

    Flux<LoanApplication> listByProcess(String processId);
}
