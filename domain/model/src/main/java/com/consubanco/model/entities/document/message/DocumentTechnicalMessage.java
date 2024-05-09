package com.consubanco.model.entities.document.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentTechnicalMessage implements IExceptionMessage {

    API_SEARCH_INTERLOCUTOR_ERROR("TE_DOCUMENT_PAYLOAD_0001", "Error when consuming searchInterlocutor API."),
    API_CUSTOMER_ERROR("TE_DOCUMENT_PAYLOAD_0002", "Error when consuming customer by process API."),
    API_ACTIVE_OFFER_ERROR("TE_DOCUMENT_PAYLOAD_0003", "Error when consuming active offer by process API."),
    CUSTOMER_HEALTH_ERROR("TE_DOCUMENT_PAYLOAD_0004", "Error when consuming customer health service."),
    OFFER_HEALTH_ERROR("TE_DOCUMENT_PAYLOAD_0005", "Error when consuming offer health service."),
    PAYLOAD_ERROR("TE_DOCUMENT_PAYLOAD_0006", "Error when processing the payload template for request."),
    API_DOCS_PREVIOUS_ERROR("TE_DOCUMENT_0001", "Error when consuming get docs previous application API.");

    private final String code;
    private final String message;

}
