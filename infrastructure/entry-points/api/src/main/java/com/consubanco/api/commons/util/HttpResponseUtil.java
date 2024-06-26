package com.consubanco.api.commons.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@UtilityClass
public class HttpResponseUtil {

    public static <T> Mono<ServerResponse> ok(T body) {
        return buildResponse(OK, body);
    }

    public static <T> Mono<ServerResponse> accepted(T body) {
        return buildResponse(ACCEPTED, body);
    }

    public static <T> Mono<ServerResponse> created(T body) {
        return buildResponse(CREATED, body);
    }


    public static <T> Mono<ServerResponse> internalError(T body) {
        return buildResponse(INTERNAL_SERVER_ERROR, body);
    }

    public static <T> Mono<ServerResponse> conflict(T body) {
        return buildResponse(CONFLICT, body);
    }

    public static <T> Mono<ServerResponse> buildResponse(HttpStatusCode status, T body) {
        return ServerResponse
                .status(status)
                .contentType(APPLICATION_JSON)
                .bodyValue(body);
    }

}
