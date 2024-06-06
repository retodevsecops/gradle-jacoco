package com.consubanco.api.services.health;

import com.consubanco.api.commons.util.HttpResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class HealthHandler {

    public Mono<ServerResponse> health(ServerRequest serverRequest) {
        return HttpResponseUtil.Ok("ok");
    }

}
