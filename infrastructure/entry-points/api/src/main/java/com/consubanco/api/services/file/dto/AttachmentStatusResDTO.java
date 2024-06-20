package com.consubanco.api.services.file.dto;

import com.consubanco.model.entities.file.constant.AttachmentStatusEnum;
import com.consubanco.model.entities.file.vo.AttachmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AttachmentStatusResDTO {

    @Schema(description = "File name as found in the directory.", requiredMode = REQUIRED)
    private AttachmentStatusEnum status;

    @Schema(description = "When status is failed, it will indicate the list of invalid attachments.", requiredMode = NOT_REQUIRED)
    private List<InvalidAttachment> invalidAttachments;

    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class InvalidAttachment {

        @Schema(description = "Attachment name.", example = "recibo-nomina-0", requiredMode = REQUIRED)
        private String name;

        @Schema(description = "Business validation error code.", example = "001", requiredMode = REQUIRED)
        private String code;

        @Schema(description = "Reason why the attachment is invalid.", example = "Not the last pay stub.", requiredMode = REQUIRED)
        private String reason;

        @Schema(description = "Identifier of the attachment in the storage.", example = "csb-venta-digital/renewal/offer/55555/attachments/recibo-nomina-0/1718905983780836", requiredMode = REQUIRED)
        private String storageId;

        @Schema(description = "Analysis identifier ocr to attachment.", example = "78614611560244378651149944825175340201", requiredMode = REQUIRED)
        private String analysisId;


    }

    public AttachmentStatusResDTO(AttachmentStatus attachmentStatus) {
        this.status = attachmentStatus.getStatus();
        this.invalidAttachments = attachmentStatus.getInvalidAttachments() != null ? toListDTO(attachmentStatus) : null;
    }

    private static List<InvalidAttachment> toListDTO(AttachmentStatus attachmentStatus) {
        return attachmentStatus.getInvalidAttachments()
                .stream()
                .map(entity -> InvalidAttachment.builder()
                        .name(entity.getName())
                        .code(entity.getCode())
                        .reason(entity.getReason())
                        .storageId(entity.getStorageId())
                        .analysisId(entity.getAnalysisId())
                        .build())
                .toList();
    }

}
