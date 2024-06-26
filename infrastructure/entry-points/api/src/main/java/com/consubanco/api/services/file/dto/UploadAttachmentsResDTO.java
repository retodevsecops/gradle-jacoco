package com.consubanco.api.services.file.dto;

import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UploadAttachmentsResDTO {

    private static final String MESSAGE_OCR = "The attachments were uploaded successfully and are being validated.";
    private static final String MESSAGE_NOT_OCR = "The attachments were uploaded successfully and documents required by the agreement were generated.";

    @Schema(description = "Message indicating the operation result.", example = "The files were uploaded successfully and are being validated.", requiredMode = REQUIRED)
    private String message;

    @Schema(description = "list of documents pending validation.", requiredMode = NOT_REQUIRED)
    private List<OcrDocumentDTO> documentsPendingValidation;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OcrDocumentDTO {

        @Schema(description = "Unique identifier of the ocr document.", example = "1", requiredMode = REQUIRED)
        private Integer id;

        @Schema(description = "Technical name of the ocr document.", example = "recibo-nomina-1", requiredMode = REQUIRED)
        private String name;

        @Schema(description = "Unique identifier of the document in storage.", example = "csb-venta-digital/renewal/offer/55555/attachments/recibo-nomina-1/1718748320914279", requiredMode = REQUIRED)
        private String storageId;

        @Schema(description = "Unique identifier of the document ocr analysis.", example = "237087543262157937651521487687218722704", requiredMode = REQUIRED)
        private String analysisId;

        @Schema(description = "Ocr document validation status.", requiredMode = REQUIRED)
        private OcrStatus validationStatus;

    }

    public UploadAttachmentsResDTO(List<OcrDocument> ocrDocuments) {
        this.message = (checkList(ocrDocuments)) ? MESSAGE_OCR : MESSAGE_NOT_OCR;
        if(checkList(ocrDocuments)) this.documentsPendingValidation = toListDTO(ocrDocuments);
    }

    private List<OcrDocumentDTO> toListDTO(List<OcrDocument> ocrDocuments) {
        return ocrDocuments.stream()
                .map(ocrDocument -> OcrDocumentDTO.builder()
                        .id(ocrDocument.getId())
                        .name(ocrDocument.getName())
                        .storageId(ocrDocument.getStorageId())
                        .analysisId(ocrDocument.getAnalysisId())
                        .validationStatus(ocrDocument.getStatus())
                        .build())
                .toList();
    }

    private boolean checkList(List<OcrDocument> ocrDocuments) {
        return ocrDocuments != null && !ocrDocuments.isEmpty();
    }
}
