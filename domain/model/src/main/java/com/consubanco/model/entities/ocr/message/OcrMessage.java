package com.consubanco.model.entities.ocr.message;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class OcrMessage {

    private static final String TYPE_NOT_FOUND = "For document %s no associated ocr document type was found.";
    private static final String API_ERROR = "The api %s response %s with body: %s.";
    private static final String OCR_NOT_FOUND = "No ocr document associated with file %s with id %s was found.";
    private static final String NOT_METADATA = "The ocr document query with id %s returned no metadata.";
    private static final String RETRIES_FAILED = "Failed to get analysis data %s after retries. %s.";
    private static final String OCR_INVALID = "The document ocr %s with analysis id %s is in status %s.";
    private static final String UNKNOWN_PERIODICITY = "Unknown of periodicity from initial pay period %s to final pay period %s. The days of difference are %s";
    private static final String INVALID_FORTNIGHT_PAY_STUB = "The fortnight pay stub must be date: %s to %s. The current receipt is from %s to %s.";
    private static final String INVALID_MONTHLY_PAY_STUB = "The monthly pay stub must be date: %s. The current receipt is from %s to %s.";

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

    public static String retriesFailed(String analysisId, String message) {
        return String.format(RETRIES_FAILED, analysisId, message);
    }

    public static String ocrInvalid(String ocrDocumentName, String analysisId, OcrStatus ocrStatus) {
        return String.format(OCR_INVALID, ocrDocumentName, analysisId, ocrStatus);
    }

    public static String unknownPeriodicity(LocalDate initialPayPeriod, LocalDate finalPayPeriod, long daysBetween) {
        return String.format(UNKNOWN_PERIODICITY, initialPayPeriod, finalPayPeriod, daysBetween);
    }

    public static String invalidMonthlyPayStub(LocalDate expectedDate, LocalDate initialPay, LocalDate finalPay) {
        return String.format(INVALID_MONTHLY_PAY_STUB, expectedDate, initialPay, finalPay);
    }

    public static String invalidFortnightPayStub(LocalDate startDate, LocalDate endDate, LocalDate initialPay, LocalDate finalPay) {
        return String.format(INVALID_FORTNIGHT_PAY_STUB, startDate, endDate, initialPay, finalPay);
    }

}
