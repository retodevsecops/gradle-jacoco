package com.consubanco.model.entities.document.message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DocumentMessage {
    private final static String DOCUMENT_NOT_FOUND = "Document %s was not found in the list of generated documents.";

    public String documentNotFound(String document) {
        return String.format(DOCUMENT_NOT_FOUND, document);
    }

}
