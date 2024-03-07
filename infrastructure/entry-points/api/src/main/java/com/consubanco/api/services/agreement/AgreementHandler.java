package com.consubanco.api.services.agreement;

import com.consubanco.api.commons.HttpResponseUtil;
import com.consubanco.usecase.agreement.AgreementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AgreementHandler {

    private final AgreementUseCase agreementUseCase;

    public Mono<ServerResponse> findByNumber(ServerRequest serverRequest) {
        return agreementUseCase.findByNumber("10001186")
                .flatMap(HttpResponseUtil::Ok);
    }

}
