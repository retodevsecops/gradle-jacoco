package com.consubanco.consumer.adapters.loan;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class CreateApplicationResponseUtil {

    private static final String KEY = "createApplicationResponseBO";

    @SuppressWarnings("unchecked")
    public static Integer getCodeResponse(Map<String, Object> response) {
        Map<String, Object> resBO = (Map<String, Object>) response.get(KEY);
        return Integer.parseInt((String) resBO.get("code"));
    }

}
