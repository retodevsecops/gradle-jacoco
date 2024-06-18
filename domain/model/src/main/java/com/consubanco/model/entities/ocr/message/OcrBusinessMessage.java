package com.consubanco.model.entities.ocr.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OcrBusinessMessage implements IExceptionMessage {

    UNDEFINED_TYPE("BE_OCR_0001", "No ocr file type defined for the document.");

    private final String code;
    private final String message;

}
