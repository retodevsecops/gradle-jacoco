package com.consubanco.model.entities.ocr.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FailureReason {

    NOT_DATA_EXTRACTED("Analysis of the document did not data extracted."),
    FAILED_GET_METADATA("Failure to get document metadata."),
    NOT_PROCESS("There is still no ocr process to validate this document."),
    FISCAL_FOLIO_NOT_FOUND("The fiscal folio was not found in the document."),
    INITIAL_PAY_NOT_FOUND("The initial payment period was not found in the document."),
    FINAL_PAY_NOT_FOUND("The final payment period was not found in the document."),
    UNKNOWN_PERIODICITY("Unknown periodicity"),
    NOT_INDEX("the name of the document pay stub has no index, example: recibo-nomina-0 index 0."),
    INVALID_DATE("The pay stub is not within the valid date range."),
    UNKNOWN_ERROR("unknown error."),
    INVALID_DATE_FORMAT("Invalid date format.");

    private final String message;

}
