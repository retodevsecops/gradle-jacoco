package com.consubanco.model.entities.ocr.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OcrTechnicalMessage implements IExceptionMessage {

    API_NOTIFY_RESPONSE_ERROR("TE_OCR_0001", "The api for ocr document notification responded with error."),
    API_NOTIFY_ERROR("TE_OCR_0002", "An error occurred with the api to notify ocr document."),
    SAVE_ALL_ERROR("TE_OCR_0003", "Error when saving ocr documents in database"),
    CONVERT_JSON_ERROR("TE_OCR_0004", "Error converting object to json."),
    CONVERT_MAP_ERROR("TE_OCR_0005", "Error converting json to map.");

    private final String code;
    private final String message;

}
