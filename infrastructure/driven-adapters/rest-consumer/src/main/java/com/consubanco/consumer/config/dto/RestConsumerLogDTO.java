package com.consubanco.consumer.config.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RestConsumerLogDTO implements Serializable {

    private static final String TITLE_DEFAULT = "Request data http";

    private static final String UNKNOWN_ENDPOINT = "Unknown endpoint";
    private static final String UNKNOWN_METHOD = "Unknown method";

    private String title;
    private RequestDTO request;
    private ResponseDTO response;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class RequestDTO implements Serializable {
        private String endpoint;
        private String method;
        private Map<String, Object> headers;
        private Object body;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class ResponseDTO implements Serializable {
        private String status;
        private Map<String, Object> headers;
        private Object body;
    }

    public RestConsumerLogDTO(WebClientResponseException exception) {
        this.title = TITLE_DEFAULT;
        this.request = RequestDTO.builder()
                .endpoint(getURIFromResponseException(exception.getRequest()))
                .method(getMethodFromResponseException(exception.getRequest()))
                .headers(extractHeaders(exception.getRequest().getHeaders()))
                .build();
        this.response = ResponseDTO.builder()
                .status(exception.getStatusCode().toString())
                .body(exception.getResponseBodyAsString())
                .headers(extractHeaders(exception.getHeaders()))
                .build();
    }

    private String getURIFromResponseException(HttpRequest request) {
        if (request != null) return request.getURI().toString();
        return UNKNOWN_ENDPOINT;
    }

    private String getMethodFromResponseException(HttpRequest request) {
        if (request != null) return request.getMethod().name();
        return UNKNOWN_METHOD;
    }

    private Map<String, Object> extractHeaders(HttpHeaders httpHeaders) {
        return httpHeaders.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        entry.getValue().size() == 1 ? entry.getValue().get(0) : entry.getValue())
                );
    }

}
