package com.consubanco.model.entities.file.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileBusinessMessage implements IExceptionMessage {

    MISSING_ATTACHMENT("BE_FILE_0001", "Missing attachments."),
    OFFER_ID_IS_NULL("BE_FILE_0002", "The offer id is required."),
    PAYLOAD_TEMPLATE_NOT_FOUND("BE_FILE_0003", "The payload template file does not exist."),
    PAYLOAD_TEMPLATE_INCORRECT("BE_FILE_0004", "The structure of template for the document generation payload is incorrect."),
    FILES_NOT_FOUND("BE_FILE_0003", "Files not found.");

    private final String code;
    private final String message;

}
