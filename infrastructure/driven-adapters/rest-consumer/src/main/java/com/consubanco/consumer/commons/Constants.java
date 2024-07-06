package com.consubanco.consumer.commons;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String CLIENT_ID_HEADER = "X-IBM-Client-Id";
    public static final String AUTH_BEARER_VALUE = "Bearer %s";
    public static final String TOKEN_RENEX_CACHE_KEY = "token-renex";
    public static final String API_KEY_OCR_HEADER = "X-api-key";
}
