package com.consubanco.consumer.config.filters;

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
public class RestConsumerLogDTO implements Serializable {

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
        private Map<String, Object> attributes;
        private Map<String, Object> headers;
        private Object body;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class ResponseDTO implements Serializable{
        private String status;
        private Map<String, Object> headers;
        private Object body;
    }

}
