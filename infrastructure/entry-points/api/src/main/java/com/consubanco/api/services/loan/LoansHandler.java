package com.consubanco.api.services.loan;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.usecase.loan.CreateApplicationLoanUseCase;
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

    public Mono<ServerResponse> createApplication(ServerRequest serverRequest) {
        String processId = serverRequest.pathVariable(PROCESS_ID);
        return createApplicationLoanUseCase.execute(processId)
                .flatMap(HttpResponseUtil::Ok);
    }

}
