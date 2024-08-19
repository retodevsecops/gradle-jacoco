package com.consubanco.model.entities.file.message;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class FileMessage {

    private static final String DELIMITER = ", ";
    private static final String MAX_SIZE = "The maximum size allowed is %s";
    private static final String FILES_EXCEED_SIZE = "The files: %s exceed the maximum size allowed which is %s MB.";
    private static final String ATTACHMENT_REQUIRED = "The attachments are required: %s.";
    private static final String FILES_INVALID_TYPES = "The files: %s are invalid file types.";
    private static final String ATTACHMENT_EXCEEDED = "The attachments: %s exceed the number of files required.";
    private static final String ATTACHMENTS_NOT_FOUND = "No uploaded attachments found for offer %s.";
    private static final String OFFER_FILES_NOT_FOUND = "Files not found for offer %s.";

    public static String maxSize(Double size) {
        return String.format(MAX_SIZE, size);
    }

    public static String filesExceedSize(List<String> files, Double size) {
        return String.format(FILES_EXCEED_SIZE, String.join(DELIMITER, files), size);
    }

    public static String attachmentRequired(List<String> files) {
        return String.format(ATTACHMENT_REQUIRED, String.join(DELIMITER, files));
    }

    public static String filesInvalidTypes(List<String> files) {
        return String.format(FILES_INVALID_TYPES, String.join(DELIMITER, files));
    }

    public static String attachmentExceeded(List<String> files) {
        return String.format(ATTACHMENT_EXCEEDED, String.join(DELIMITER, files));
    }

    public static String attachmentsNotFound(String offerId) {
        return String.format(ATTACHMENTS_NOT_FOUND, offerId);
    }

    public static String offerFilesNotFound(String offerId) {
        return String.format(OFFER_FILES_NOT_FOUND, offerId);
    }


}
