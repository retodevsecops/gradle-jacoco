package com.consubanco.model.entities.ocr.message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OcrMessage {

    private static final String TYPE_NOT_FOUND = "For document %s no associated ocr document type was found.";
    private static final String API_ERROR = "The api %s response %s with body: %s";

    public static String typeNotFound(final String documentName) {
        return String.format(TYPE_NOT_FOUND, documentName);
    }

    public static String apiError(String api, String status, String body) {
        return String.format(API_ERROR, api, status, body);
    }

}
