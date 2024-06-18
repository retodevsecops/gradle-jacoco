package com.consubanco.consumer.services.promoter.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@UtilityClass
public class SearchInterlocutorResUtil {

    private static final String RESPONSE_OBJECT_KEY = "searchInterlocutorResBO";
    private static final String PEOPLE_DATA_KEY = "people";
    private static final String CODE_RESPONSE_KEY = "code";

    @SuppressWarnings("unchecked")
    public Boolean checkIfSuccessResponse(Map<String, Object> response) {
        Map<String, Object> resBO = (Map<String, Object>) response.get(RESPONSE_OBJECT_KEY);
        String code = (String) resBO.get(CODE_RESPONSE_KEY);
        return HttpStatus.OK.value() == Integer.parseInt(code);
    }

    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> getDataInterlocutor(Map<String, Object> response) {
        return Mono.just(response.get(RESPONSE_OBJECT_KEY))
                .map(resBO -> (Map<String, Object>) resBO)
                .map(map -> (List<Map<String, Object>>) map.get(PEOPLE_DATA_KEY))
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0));
    }

}
