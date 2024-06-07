package com.consubanco.model.entities.file.message;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class FileMessage {

    private final static String DELIMITER = ", ";
    private final static String MAX_SIZE = "The maximum size allowed is %s";
    private final static String FILES_EXCEED_SIZE = "The files: %s exceed the maximum size allowed which is %s MB.";
    private final static String ATTACHMENT_REQUIRED = "The attachments are required: %s.";
    private final static String FILES_INVALID_TYPES = "The files: %s are invalid file types.";
    private final static String ATTACHMENT_EXCEEDED = "The attachments: %s exceed the number of files required.";

    public static String maxSize(Double size){
        return String.format(MAX_SIZE, size);
    }

    public static String filesExceedSize(List<String> files, Double size){
        return String.format(FILES_EXCEED_SIZE, String.join(DELIMITER, files), size);
    }

    public static String attachmentRequired(List<String> files){
        return String.format(ATTACHMENT_REQUIRED, String.join(DELIMITER, files));
    }

    public static String filesInvalidTypes(List<String> files){
        return String.format(FILES_INVALID_TYPES, String.join(DELIMITER, files));
    }

    public static String attachmentExceeded(List<String> files){
        return String.format(ATTACHMENT_EXCEEDED, String.join(DELIMITER, files));
    }


}
