package com.consubanco.model.entities.document.message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DocumentMessage {
    private final static String DOCUMENT_NOT_FOUND = "Document %s was not found in the list of generated documents.";
    private final static String FORMAT_NOM151 = "Should have been answered with the format: %s";
    public String documentNotFound(String document) {
        return String.format(DOCUMENT_NOT_FOUND, document);
    }

    public String formatNom151(String regex) {
        return String.format(FORMAT_NOM151, regex);
    }

}
