package com.consubanco.consumer.services.promoter.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@UtilityClass
public class BranchesByPromoterResUtil {

    public static final String BRANCHES = "branches";
    private static final String RESPONSE_OBJECT_KEY = "branchesByPromotorResponseBO";
    private static final String PROMOTER_DATA_KEY = "promotorData";
    private static final String BRANCHES_KEY = "sucursales";
    private static final String CODE_RESPONSE_KEY ="code";

    @SuppressWarnings("unchecked")
    public Boolean checkIfSuccessResponse(Map<String, Object> response) {
        Map<String, Object> resBO = (Map<String, Object>) response.get(RESPONSE_OBJECT_KEY);
        String code = (String) resBO.get(CODE_RESPONSE_KEY);
        return HttpStatus.OK.value() == Integer.parseInt(code);
    }

    @SuppressWarnings("unchecked")
    public Mono<List<Map<String, Object>>> getBranches(Map<String, Object> response) {
        return Mono.just(response.get(RESPONSE_OBJECT_KEY))
                .map(resBO -> (Map<String, Object>) resBO)
                .map(map -> (List<Map<String, Object>>) ((Map<String, Object>) map.get(PROMOTER_DATA_KEY)).get(BRANCHES_KEY));
    }

}
