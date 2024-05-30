package com.consubanco.logger;

import com.consubanco.logger.dto.LogApiErrorDTO;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@UtilityClass
public class LoggerUtil {

    public LogApiErrorDTO buildLogError(ServerRequest serverRequest, Throwable error) {
        return LogApiErrorDTO.builder()
                .method(serverRequest.method().name())
                .headers(serverRequest.headers().asHttpHeaders())
                .queryParams(serverRequest.queryParams())
                .pathParams(serverRequest.pathVariables())
                .error(buildStackTrace(error))
                .build();
    }

    public Map<String, Object> buildStackTrace(Throwable error) {
        Map<String, Object> map = new HashMap<>();
        Optional.ofNullable(error.getStackTrace()).ifPresent(trace -> map.put("stackTrace", trace));
        Optional.ofNullable(error.getMessage()).ifPresent(message -> map.put("message", message));
        map.put("exception", error.toString());
        return map;
    }

}
