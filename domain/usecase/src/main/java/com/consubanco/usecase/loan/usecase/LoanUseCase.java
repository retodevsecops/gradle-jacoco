package com.consubanco.usecase.loan.usecase;

import com.consubanco.model.entities.loan.LoanApplication;
import com.consubanco.model.entities.loan.gateway.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class LoanUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    public Flux<LoanApplication> listByProcess(String processId) {
        return loanApplicationRepository.listByProcess(processId);
    }

}
