package com.consubanco.api.services.document.dto;

import com.consubanco.model.entities.document.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenerateDocumentRequestDTO {

    @Schema(description = "Payload with all the parameters to map documents.")
    private Map<String, Object> payload;

    @Schema(description = "Documents to be contained in the resulting unified pdf document.", example ="[\"formato-unico-caratula\", \"domiciliacion\"]" )
    private List<String> documents;

    public List<Document> documentsToEntity() {
        return this.documents.stream()
                .map(document -> Document.builder()
                        .technicalName(document)
                        .build())
                .collect(Collectors.toList());
    }

}
