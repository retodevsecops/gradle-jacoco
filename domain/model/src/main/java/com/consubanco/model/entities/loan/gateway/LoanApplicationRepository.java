package com.consubanco.model.entities.loan.gateway;

import com.consubanco.model.entities.loan.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {
    Mono<Void> saveApplication(LoanApplication loanApplication);
    Flux<LoanApplication> listByProcess(String processId);
}
