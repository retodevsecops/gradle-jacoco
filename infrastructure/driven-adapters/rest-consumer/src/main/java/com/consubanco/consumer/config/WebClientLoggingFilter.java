package com.consubanco.consumer.config;

import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

import static com.consubanco.consumer.config.message.RestConsumerTechnicalMessage.CONVERSION_ERROR;

@Configuration
@RequiredArgsConstructor
public class WebClientLoggingFilter implements ExchangeFilterFunction {

    private final CustomLogger logger;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return next.exchange(request)
                .flatMap(response -> printLog(request, response));
    }

    private Mono<ClientResponse> printLog(ClientRequest request, ClientResponse response) {
        return response.bodyToMono(Object.class)
                .doOnNext(responseBody -> orchestrateLog(request, response, responseBody))
                .map(this::convertObjectToString)
                .map(body -> response.mutate().body(body).build());
    }

    private <T> void orchestrateLog(ClientRequest request, ClientResponse response, T body) {
        if (response.statusCode().isError()) {
            buildLogError(request, response, body);
        }
        buildLogInfo(request, response, body);
    }

    private <T> void buildLogError(ClientRequest request, ClientResponse response, T body) {
        String title = "API request information when is error.";
        logger.error(buildDataLog(request, response, body, title));
    }

    private <T> void buildLogInfo(ClientRequest request, ClientResponse response, T body) {
        String title = "API request information when is successful.";
        logger.info(buildDataLog(request, response, body, title));
    }

    private <T> RestConsumerLogDTO buildDataLog(ClientRequest req, ClientResponse res, T body, String title) {
        return RestConsumerLogDTO.builder()
                .title(title)
                .request(buildLogRequestDTO(req))
                .response(buildLogResponseDTO(res, body))
                .build();
    }

    public RestConsumerLogDTO.RequestDTO buildLogRequestDTO(ClientRequest request) {
        return RestConsumerLogDTO.RequestDTO.builder()
                .endpoint(request.url().toString())
                .method(request.method().name())
                .headers(extractHeaders(request.headers()))
                .body(request.body())
                .build();
    }

    public <T> RestConsumerLogDTO.ResponseDTO buildLogResponseDTO(ClientResponse response, T body) {
        return RestConsumerLogDTO.ResponseDTO.builder()
                .status(response.statusCode().toString())
                .headers(extractHeaders(response.headers().asHttpHeaders()))
                .body(body)
                .build();
    }

    public static Map<String, Object> extractHeaders(HttpHeaders httpHeaders) {
        return httpHeaders.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        entry.getValue().size() == 1 ? entry.getValue().get(0) : entry.getValue())
                );
    }

    private String convertObjectToString(Object bodyObject) {
        try {
            return objectMapper.writeValueAsString(bodyObject);
        } catch (Exception e) {
            throw ExceptionFactory.buildTechnical(CONVERSION_ERROR);
        }
    }

}
