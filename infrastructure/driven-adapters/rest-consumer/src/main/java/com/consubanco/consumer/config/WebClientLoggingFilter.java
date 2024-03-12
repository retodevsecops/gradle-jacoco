package com.consubanco.consumer.config;

import com.consubanco.logger.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class WebClientLoggingFilter implements ExchangeFilterFunction {

    private final CustomLogger logger;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        logger.info("Request: {} {}" + request.url());
        return next.exchange(request)
                .flatMap(response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                logger.info("response body " + body);
                                return Mono.just(ClientResponse.from(response).body(body).build());
                            });
                });
    }
}
