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
    TEMPLATE_INCORRECT("BE_FILE_0004", "The structure of template for the document generation payload is incorrect."),
    FILES_NOT_FOUND("BE_FILE_0005", "Files not found."),
    FILE_NOT_JSON("BE_FILE_0006", "The file must be a valid json file."),
    FILE_NOT_FTL("BE_FILE_0007", "The file must be a valid ftl file."),
    ATTACHMENT_INVALID_SIZE("BE_FILE_0008", "Files exceed the maximum allowed size."),
    ATTACHMENT_INVALID_TYPE("BE_FILE_0009", "Attachments come with not allowed file types."),
    DATA_MISSING_TO_UPLOAD("BE_FILE_0010", "Missing data to upload the file."),
    MIN_INVALID_SIZE("BE_FILE_0011", "Minimum file size is invalid."),
    INCOMPLETE_DATA("BE_FILE_0012", "The file data is incomplete"),
    ATTACHMENT_EXCEEDED("BE_FILE_0013", "The number of files for the attachment exceeds the allowed.");

    private final String code;
    private final String message;

}
