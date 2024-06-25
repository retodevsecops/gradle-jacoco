package com.consubanco.model.entities.ocr.message;

import com.consubanco.model.entities.file.File;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OcrMessage {

    private static final String TYPE_NOT_FOUND = "For document %s no associated ocr document type was found.";
    private static final String API_ERROR = "The api %s response %s with body: %s";
    private static final String OCR_NOT_FOUND = "No ocr document associated with file %s with id %s was found.";
    private static final String NOT_METADATA = "The ocr document query with id %s returned no metadata";

    public static String typeNotFound(final String documentName) {
        return String.format(TYPE_NOT_FOUND, documentName);
    }

    public static String apiError(String api, String status, String body) {
        return String.format(API_ERROR, api, status, body);
    }

    public static String ocrNotFound(File file) {
        return String.format(OCR_NOT_FOUND, file.getName(), file.getId());
    }

    public static String notMetadata(String analysisId) {
        return String.format(NOT_METADATA, analysisId);
    }

}
