package com.consubanco.api.commons.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@UtilityClass
public class ParamsValidator {

    private static final String NON_NULL = "paramName must not be null";
    private static final String MISSING_PARAM = "The parameter %s is required";
    private static final String NOT_URL = "The parameter must be a valid url.";

    public static Mono<String> queryParam(ServerRequest request, String paramName) {
        Objects.requireNonNull(paramName, NON_NULL);
        return request.queryParam(paramName)
                .filter(paramValue -> !paramValue.trim().isEmpty())
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new ResponseStatusException(BAD_REQUEST, String.format(MISSING_PARAM, paramName))));
    }

    public static Mono<String> paramIsUrl(String paramValue) {
        return Mono.fromSupplier(() -> {
            try {
                return new URL(paramValue).toString();
            } catch (MalformedURLException e) {
                throw new ResponseStatusException(BAD_REQUEST, NOT_URL);
            }
        });
    }

    public static Mono<String> checkQueryParamIsUrl(ServerRequest request, String paramName) {
        return queryParam(request, paramName)
                .flatMap(ParamsValidator::paramIsUrl);
    }

}
