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
    API_DOCS_PREVIOUS_ERROR("TE_DOCUMENT_0007", "Error when consuming get docs previous application API."),
    API_DOCS_PREVIOUS_TIMEOUT("TE_DOCUMENT_0008", "The API opp-service/findDocsPreviusApplication did not respond in the expected time."),
    API_NOM151_ERROR("TE_DOCUMENT_0009", "Error consuming the nom151 soap API."),
    NOM151_UNEXPECTED_FORMAT("TE_DOCUMENT_0010", "The nom151 api did not respond with the expected format."),
    API_NOM151_RESPONSE_ERROR("TE_DOCUMENT_0011", "The api nom151 responded with error."),
    API_BRANCHES_BY_PROMOTER_ERROR("TE_DOCUMENT_0012", "Error when consuming branches by promoter API."),
    API_BIOMETRIC_TASK("TE_DOCUMENT_PAYLOAD_0013", "Error when consuming biometric task API.");

    private final String code;
    private final String message;

}
