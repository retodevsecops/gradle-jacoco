package com.consubanco.consumer.adapters.document.dto;

import com.consubanco.model.entities.document.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class GenerateDocumentRequestDTO {

    private List<DocumentDTO> documents;
    private Map<String, Object> payload;

    public GenerateDocumentRequestDTO(List<Document> documents, Map<String, Object> payload) {
        this.documents = documentsToDTO(documents);
        this.payload = payload;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocumentDTO implements Serializable {
        private String technicalName;
    }

    private List<DocumentDTO> documentsToDTO(List<Document> documents) {
        return documents.stream()
                .map(document -> DocumentDTO.builder()
                        .technicalName(document.getTechnicalName())
                        .build())
                .collect(Collectors.toList());
    }

}
