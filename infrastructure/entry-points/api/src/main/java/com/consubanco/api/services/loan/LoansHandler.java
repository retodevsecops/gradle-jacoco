package com.consubanco.api.services.loan;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.loan.constants.LoanHeaderParams;
import com.consubanco.api.services.loan.dto.LoanApplicationResDTO;
import com.consubanco.usecase.loan.CreateApplicationLoanUseCase;
import com.consubanco.usecase.loan.LoanUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.consubanco.api.services.loan.constants.LoanPathParams.PROCESS_ID;

@Component
@RequiredArgsConstructor
public class LoansHandler {

    private final CreateApplicationLoanUseCase createApplicationLoanUseCase;
    private final LoanUseCase loanUseCase;

    public Mono<ServerResponse> createApplication(ServerRequest serverRequest) {
        String processId = serverRequest.pathVariable(PROCESS_ID);
        String otpCode = serverRequest.headers().firstHeader(LoanHeaderParams.OTP);
        return createApplicationLoanUseCase.execute(processId, otpCode)
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> listByProcess(ServerRequest serverRequest) {
        String processId = serverRequest.pathVariable(PROCESS_ID);
        return loanUseCase.listByProcess(processId)
                .map(LoanApplicationResDTO::new)
                .collectList()
                .flatMap(HttpResponseUtil::Ok);
    }

}
