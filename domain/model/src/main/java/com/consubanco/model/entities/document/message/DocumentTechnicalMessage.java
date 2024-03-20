package com.consubanco.model.entities.document.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentTechnicalMessage implements IExceptionMessage {

    API_ERROR("TE_DOCUMENT_0001", "Error when consulting the promoter API to generate document.");

    private final String code;
    private final String message;

}
