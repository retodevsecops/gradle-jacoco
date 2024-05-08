package com.consubanco.api.services.loan;

import com.consubanco.api.commons.util.HttpResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoansHandler {

    public Mono<ServerResponse> createApplication(ServerRequest serverRequest) {
        return HttpResponseUtil.Ok(Map.of("message","El unico limite para tus logros esta en tu propia mente."));
    }

}
