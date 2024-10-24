package com.consubanco.model.entities.ocr.message;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class OcrMessage {

    private static final String TYPE_NOT_FOUND = "For document %s no associated ocr document type was found.";
    private static final String API_ERROR = "The api %s response %s with body: %s.";
    private static final String OCR_NOT_ASSOCIATED = "No ocr document associated with file %s with id %s was found.";
    private static final String NOT_METADATA = "The ocr document query with id %s returned no metadata.";
    private static final String RETRIES_FAILED = "Failed to get analysis data %s after retries. %s.";
    private static final String OCR_INVALID = "The document ocr %s with analysis id %s is in status %s.";
    private static final String UNKNOWN_PERIODICITY = "Unknown of periodicity from initial pay period %s to final pay period %s. The days of difference are %s.";
    private static final String INVALID_FORTNIGHT_PAY_STUB = "The fortnight pay stub must be date: %s to %s. The current receipt is from %s to %s.";
    private static final String INVALID_MONTHLY_PAY_STUB = "The monthly pay stub must be date: %s to %s. The current receipt is from %s to %s.";
    private static final String OCR_NOT_FOUND = "No ocr document was found with the analysis id %s.";
    private static final String NOT_FOUND_BY_STORAGE_ID = "No ocr document was found with the storage id %s.";
    private static final String NOT_FOUND_BY_PROCESS = "No ocr documents was found with the process id %s.";
    private static final String INVALID_CONFIDENCE = "The confidence level of the %s data is %s and must be equal to or higher than %s.";
    private static final String EXPIRED_ADDRESS_VALIDITY = "Address validity: %s its more than %s months ago";
    private static final String DATA_NOT_FOUND = "From the extracted data of the document, the following data was not found: %s.";

    public static String typeNotFound(final String documentName) {
        return String.format(TYPE_NOT_FOUND, documentName);
    }

    public static String apiError(String api, String status, String body) {
        return String.format(API_ERROR, api, status, body);
    }

    public static String ocrNotAssociated(File file) {
        return String.format(OCR_NOT_ASSOCIATED, file.getName(), file.getId());
    }

    public static String notFoundByAnalysisId(String analysisId) {
        return String.format(OCR_NOT_FOUND, analysisId);
    }

    public static String notFoundByProcessId(String processId) {
        return String.format(NOT_FOUND_BY_PROCESS, processId);
    }

    public static String notFoundByStorageId(String storageId) {
        return String.format(NOT_FOUND_BY_STORAGE_ID, storageId);
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

    public static String invalidMonthlyPayStub(LocalDate startDate, LocalDate endDate, LocalDate initialPay, LocalDate finalPay) {
        return String.format(INVALID_MONTHLY_PAY_STUB, startDate, endDate, initialPay, finalPay);
    }

    public static String expiredAddressValidity(LocalDate validity, int validityMonths) {
        return String.format(EXPIRED_ADDRESS_VALIDITY, validity.toString(), validityMonths);
    }

    public static String invalidFortnightPayStub(LocalDate startDate, LocalDate endDate, LocalDate initialPay, LocalDate finalPay) {
        return String.format(INVALID_FORTNIGHT_PAY_STUB, startDate, endDate, initialPay, finalPay);
    }

    public static String invalidConfidence(String dataName, double confidence, double confidenceAllow) {
        return String.format(INVALID_CONFIDENCE, dataName, confidence, confidenceAllow);
    }

    public static String dataNotFound(String dataName) {
        return String.format(DATA_NOT_FOUND, dataName);
    }

}
