package com.consubanco.model.entities.document.message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DocumentMessage {

    public static final String DOCUMENT_NOT_FOUND = "Document %s needed to build the compound document %s was not found.";
    public static final String FORMAT_NOM151 = "Should have been answered with the format: %s";
    public static final String GENERATE_CNCA = "First generate cnca letter to generate the compound documents.";
    public static final String DOCUMENTS_NOT_GENERATED = "No documents have been generated for offer %s.";

    public String documentNotFound(String document, String compoundDocument) {
        return String.format(DOCUMENT_NOT_FOUND, document, compoundDocument);
    }

    public String formatNom151(String regex) {
        return String.format(FORMAT_NOM151, regex);
    }

    public String documentNotGenerated(String offerId) {
        return String.format(DOCUMENTS_NOT_GENERATED, offerId);
    }

}
