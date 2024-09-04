package com.consubanco.api.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LogErrorDTO {

    private String title;
    private Request request;
    private Response response;
    private Throwable error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Request implements Serializable {
        private String endpoint;
        private String method;
        private Map<String, Object> headers;
        private Object body;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Response implements Serializable {
        private String status;
        private Object body;
    }

}
