package com.consubanco.model.entities.ocr.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FailureReason {

    NOT_DATA_EXTRACTED("Analysis of the document did not data extracted."),
    FAILED_GET_METADATA("Failure to get document metadata.");

    private final String message;
}
