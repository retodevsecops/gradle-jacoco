package com.consubanco.model.entities.ocr.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OcrBusinessMessage implements IExceptionMessage {

    UNDEFINED_TYPE("BE_OCR_0001", "No ocr file type defined for the document."),
    OCR_NOT_FOUND("BE_OCR_0002", "No ocr document associated with attachment was found."),
    OCR_INVALID("BE_OCR_0003", "Ocr document is in invalid status."),
    INVALID_MONTH("BE_OCR_0004", "Not recognized as a valid month"),
    OCR_DOCUMENT_NOT_FOUND("BE_OCR_0005", "No ocr document found with this id."),
    ID_ANALYSIS_REQUIRED("BE_OCR_0006", "The analysis identifier is required."),
    DOCUMENT_NAME_REQUIRED("BE_OCR_0007", "The document technical name is required."),
    STORAGE_ID_REQUIRED("BE_OCR_0008", "The storage id of document is required."),
    PROCESS_ID_REQUIRED("BE_OCR_0009", "The process identifier is required."),
    INVALID_DATE_FORMAT("BE_OCR_0009", "Invalid Date format in OCR meta-data response");

    private final String code;
    private final String message;

}
