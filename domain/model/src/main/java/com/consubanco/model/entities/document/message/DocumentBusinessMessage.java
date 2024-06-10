package com.consubanco.model.entities.document.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentBusinessMessage implements IExceptionMessage {

    DOCUMENT_NOT_FOUND("BE_DOCUMENT_0001", "The file required to build the composite document was not found."),
    NOT_GENERATED("BE_DOCUMENT_0002", "The document was not generated."),
    CNCA_NOT_GENERATED("BE_DOCUMENT_0003", "Cannot create letter of credit because the credit not exist or not comply with the business rules."),
    FAILED_LOAD_DOCUMENT("BE_DOCUMENT_0004", "The document could not be uploaded to generate the nom151."),
    CNCA_NOT_FOUND("BE_DOCUMENT_0005", "The CNCA letter not found."),
    DOCUMENTS_NOT_GENERATED("BE_DOCUMENT_0006", "Offer documents not found.");

    private final String code;
    private final String message;

}
