package com.consubanco.model.entities.file.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtil {

    public String getFileNameFromPath(String documentPath) {
        String[] parts = documentPath.split("/");
        return parts[parts.length - 1];
    }

}
