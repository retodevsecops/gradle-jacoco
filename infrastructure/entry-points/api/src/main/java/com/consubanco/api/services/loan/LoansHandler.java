package com.consubanco.api.services.loan;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.loan.constants.LoanHeaderParams;
import com.consubanco.api.services.loan.dto.LoanApplicationResDTO;
import com.consubanco.model.entities.otp.Otp;
import com.consubanco.usecase.loan.usecase.BuildDataForApplicationUseCase;
import com.consubanco.usecase.loan.usecase.CreateApplicationLoanUseCase;
import com.consubanco.usecase.loan.usecase.LoanUseCase;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
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
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final BuildDataForApplicationUseCase buildDataForApplicationUseCase;

    public Mono<ServerResponse> createApplication(ServerRequest serverRequest) {
        String processId = serverRequest.pathVariable(PROCESS_ID);
        Otp otp = buildOtp(serverRequest);
        return createApplicationLoanUseCase.execute(processId, otp)
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> listByProcess(ServerRequest serverRequest) {
        String processId = serverRequest.pathVariable(PROCESS_ID);
        return loanUseCase.listByProcess(processId)
                .map(LoanApplicationResDTO::new)
                .collectList()
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> applicationData(ServerRequest serverRequest) {
        String processId = serverRequest.pathVariable(PROCESS_ID);
        return getProcessByIdUseCase.execute(processId)
                .flatMap(buildDataForApplicationUseCase::execute)
                .flatMap(HttpResponseUtil::Ok);
    }

    private Otp buildOtp(ServerRequest request) {
        return Otp.builder()
                .code(request.headers().firstHeader(LoanHeaderParams.OTP))
                .latitude(request.headers().firstHeader(LoanHeaderParams.LATITUDE))
                .longitude(request.headers().firstHeader(LoanHeaderParams.LONGITUDE))
                .ip(request.headers().firstHeader(LoanHeaderParams.IP))
                .userAgent(request.headers().firstHeader(LoanHeaderParams.USER_AGENT))
                .build();
    }

}
