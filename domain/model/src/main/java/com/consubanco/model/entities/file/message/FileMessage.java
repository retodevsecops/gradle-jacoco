package com.consubanco.model.entities.file.message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileMessage {
    private final static String MAX_SIZE = "The maximum size allowed is %s";
    public final static String DATA_MISSING = "The name, content and extension fields must be defined.";

    public String maxSize(Double size){
        return String.format(MAX_SIZE, size);
    }

}
