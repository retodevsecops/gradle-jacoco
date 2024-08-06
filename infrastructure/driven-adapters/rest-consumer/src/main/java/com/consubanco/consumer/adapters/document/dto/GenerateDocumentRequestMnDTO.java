package com.consubanco.consumer.adapters.document.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GenerateDocumentRequestMnDTO {

    private List<String> documents;
    private Map<String, Object> payload;

    public GenerateDocumentRequestMnDTO(List<String> documents, Map<String, Object> payload) {
        this.documents = documents;
        this.payload = enableIndividualDocumentsInPayload(payload);
    }

    private Map<String, Object> enableIndividualDocumentsInPayload(Map<String, Object> payload) {
        payload.put("detach", true);
        return payload;
    }

}
