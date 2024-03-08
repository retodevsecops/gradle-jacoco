package com.consubanco.api.services.agreement;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.usecase.agreement.AgreementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AgreementHandler {

    private static final String AGREEMENT_NUMBER_PATH_PARAM = "agreementNumber";
    private final AgreementUseCase agreementUseCase;

    public Mono<ServerResponse> findByNumber(ServerRequest serverRequest) {
        String agreementNumber = serverRequest.pathVariable(AGREEMENT_NUMBER_PATH_PARAM);
        return agreementUseCase.findByNumber(agreementNumber)
                .flatMap(HttpResponseUtil::Ok);
    }

}
