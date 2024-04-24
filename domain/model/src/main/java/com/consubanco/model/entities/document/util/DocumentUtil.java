package com.consubanco.model.entities.document.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DocumentUtil {

    public String getDocumentNameFromPath(String documentPath) {
        String[] parts = documentPath.split("/");
        return parts[parts.length - 1];
    }

}
