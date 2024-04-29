package com.consubanco.api.services.agreement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AttachmentResDTO {

    @Schema(description = "Document name.", example = "Recibos de nomina", requiredMode = REQUIRED)
    private String name;

    @Schema(description = "Document identifier technical name.", example = "recibo-nomina", requiredMode = REQUIRED)
    private String technicalName;

    @Schema(description = "indicates the maximum amount of files allowed.", example = "2", requiredMode = REQUIRED)
    private String maxFiles;

    @Schema(description = "Indicates if the attachment should be validated with OCR.", requiredMode = REQUIRED)
    private Boolean isOcr;

    @Schema(description = "Indicates if the attached document is required.", requiredMode = REQUIRED)
    private Boolean isRequired;

    @Schema(description = "Indicates pdf or jpg file type", example = "[\"JPG\",\"PDF\"]", requiredMode = REQUIRED)
    private List<String> typeFile;
}
