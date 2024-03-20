package com.consubanco.api.services.document.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenerateDocumentResponseDTO {

    @Schema(description = "Url of generated pdf document", example = "https://storage.googleapis.com/700245.pdf")
    private String document;
}
