package com.consubanco.consumer.adapters.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GenerateDocumentResponseDTO {

    private String path;
    private String publicUrl;

}
