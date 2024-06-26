package com.consubanco.api.commons.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static reactor.core.publisher.Mono.just;

@UtilityClass
public class ParamsUtil {
    private static final String DELIMITER = ":";
    public static final String MESSAGE_ID = "message-id";
    public static final String CHANNEL = "channel";
    public static final String DEFAULT_VALUE = "undefined";

    private static Optional<String> ofEmpty(String value) {
        return (value == null || value.isEmpty()) ? Optional.empty() : Optional.of(value);
    }

    public static Mono<String> getMessageId(ServerRequest serverRequest) {
        return getHeader(serverRequest, MESSAGE_ID);
    }

    public static Mono<String> getChannel(ServerRequest serverRequest) {
        return getHeader(serverRequest, CHANNEL);
    }

    public static Mono<String> getHeader(ServerRequest serverRequest, String header) {
        return just(serverRequest)
                .map(request -> ofEmpty(request.headers().firstHeader(header)).orElse(DEFAULT_VALUE));
    }

    public static Mono<Map<String, String>> entrySetToMap(Set<Map.Entry<String, List<String>>> entrySet) {
        return just(entrySet.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, v -> String.join(",", v.getValue()))));
    }

    public static Mono<String> getDomain(ServerRequest serverRequest) {
        return Mono.just(serverRequest.method().name())
                .map(method -> String.join(DELIMITER, method, serverRequest.uri().toString()))
                .map(String::toLowerCase);
    }

}
