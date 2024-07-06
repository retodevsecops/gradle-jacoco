package com.consubanco.model.entities.ocr.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OcrTechnicalMessage implements IExceptionMessage {

    API_NOTIFY_RESPONSE_ERROR("TE_OCR_0001", "The api for ocr document notification responded with error."),
    API_NOTIFY_ERROR("TE_OCR_0002", "An error occurred with the api to notify ocr document."),
    SAVE_ALL_ERROR("TE_OCR_0003", "Error when saving ocr documents in database."),
    CONVERT_JSON_ERROR("TE_OCR_0004", "Error converting object to json."),
    CONVERT_MAP_ERROR("TE_OCR_0005", "Error converting json to map."),
    API_GET_METADATA_RESPONSE_ERROR("TE_OCR_0006", "The api for get ocr document data responded with error."),
    API_GET_METADATA_ERROR("TE_OCR_0007", "An error occurred with the api to get ocr document data."),
    UPDATE_ERROR("TE_OCR_0008", "Error when update  ocr documents in database."),
    FIND_ERROR("TE_OCR_0009", "Error when find ocr documents in database."),
    NOT_METADATA("TE_OCR_0010", "No metadata returned."),
    METADATA_RETRIES("TE_OCR_0011", "Failed to get analysis data after retries."),
    API_REQUEST_ERROR("TE_OCR_0012", "An error occurred with the api request.");

    private final String code;
    private final String message;

}
