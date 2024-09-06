package com.consubanco.api.services.ocr.dto;

import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OcrDocumentResDTO {

    @Schema(description = "Unique identifier of the ocr document.", example = "1", requiredMode = REQUIRED)
    private Integer id;

    @Schema(description = "Technical name of the ocr document.", example = "recibo-nomina-1", requiredMode = REQUIRED)
    private String name;

    @Schema(description = "Unique identifier of the document in storage.", example = "csb-venta-digital/renewal/offer/55555/attachments/recibo-nomina-1/1718748320914279", requiredMode = REQUIRED)
    private String storageId;

    @Schema(description = "Route in storage.", example = "csb-venta-digital/renewal/offer/55555/attachments/recibo-nomina-1/1718748320914279", requiredMode = REQUIRED)
    private String storageRoute;

    @Schema(description = "Process identifier.", example = "adsfdfsd-gfdsgfd-gfdsgfd", requiredMode = REQUIRED)
    private String processId;

    @Schema(description = "Unique identifier of the document ocr analysis.", example = "237087543262157937651521487687218722704", requiredMode = REQUIRED)
    private String analysisId;

    @Schema(description = "Ocr document validation status.", requiredMode = REQUIRED)
    private OcrStatus status;

    @Schema(description = "Data extracted from the file.", requiredMode = REQUIRED)
    private List<OcrDataDTO> data;

    @Schema(description = "Validation error code.", example = "adsfdfsd-gfdsgfd-gfdsgfd", requiredMode = NOT_REQUIRED)
    private String failureCode;

    @Schema(description = "Cause of validation failure.", example = "adsfdfsd-gfdsgfd-gfdsgfd", requiredMode = NOT_REQUIRED)
    private String failureReason;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Creation date of the analysis ocr document.", example = "yyyy-MM-dd HH:mm:ss", requiredMode = REQUIRED)
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Creation date of the analysis ocr document.", example = "yyyy-MM-dd HH:mm:ss", requiredMode = REQUIRED)
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OcrDataDTO {

        @Schema(description = "Name of extracted data.", requiredMode = REQUIRED)
        private String name;

        @Schema(description = "Confidence of the extracted data.", requiredMode = REQUIRED)
        private Double confidence;

        @Schema(description = "Extracted data value.", requiredMode = REQUIRED)
        private String value;

    }

    public OcrDocumentResDTO(OcrDocument ocrDocument) {
        this.id = ocrDocument.getId();
        this.name = ocrDocument.getName();
        this.storageId = ocrDocument.getStorageId();
        this.storageRoute = ocrDocument.getStorageRoute();
        this.processId = ocrDocument.getProcessId();
        this.status = ocrDocument.getStatus();
        this.failureCode = ocrDocument.getFailureCode();
        this.failureReason = ocrDocument.getFailureReason();
        this.createdAt = ocrDocument.getCreatedAt();
        this.updatedAt = ocrDocument.getUpdatedAt();
        this.analysisId = ocrDocument.getAnalysisId();
        this.data = dataIsNotNull(ocrDocument) ? getData(ocrDocument) : Collections.emptyList();
    }

    public boolean dataIsNotNull(OcrDocument ocrDocument) {
        return ocrDocument.getData() != null && !ocrDocument.getData().isEmpty();
    }

    private List<OcrDataDTO> getData(OcrDocument ocrDocument) {
        return ocrDocument.getData().stream()
                .map(ocrData -> OcrDataDTO.builder()
                        .name(ocrData.getName())
                        .value(ocrData.getValue())
                        .confidence(ocrData.getConfidence())
                        .build())
                .toList();
    }
}
