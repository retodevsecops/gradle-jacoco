package com.consubanco.model.entities.file.vo;


import com.consubanco.model.entities.file.constant.AttachmentStatusEnum;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentStatus {

    private AttachmentStatusEnum status;
    private List<InvalidAttachment> invalidAttachments;

    public AttachmentStatus(List<OcrDocument> ocrDocuments) {
        this.status = getStatus(ocrDocuments);
        this.invalidAttachments = getListInvalidDocuments(ocrDocuments);
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvalidAttachment {
        private String name;
        private String code;
        private String reason;
        private String storageId;
        private String analysisId;
    }

    private AttachmentStatusEnum getStatus(List<OcrDocument> ocrDocuments) {
        if (ocrDocuments == null || ocrDocuments.isEmpty()) return AttachmentStatusEnum.SUCCESS;
        return isAllPending(ocrDocuments) ? AttachmentStatusEnum.PENDING : AttachmentStatusEnum.FAILED;
    }

    private static boolean isAllPending(List<OcrDocument> ocrDocuments) {
        return ocrDocuments.stream()
                .allMatch(ocrDocument -> ocrDocument.getStatus().equals(OcrStatus.PENDING));
    }

    private List<InvalidAttachment> getListInvalidDocuments(List<OcrDocument> ocrDocuments) {
        return ocrDocuments.stream()
                .filter(ocrDocument -> ocrDocument.getStatus().equals(OcrStatus.FAILED))
                .map(ocrDocument -> InvalidAttachment.builder()
                        .name(ocrDocument.getName())
                        .code("NO SABEMOS AUN COMO ENVIAR ESTO")
                        .reason(ocrDocument.getDetail())
                        .storageId(ocrDocument.getStorageId())
                        .analysisId(ocrDocument.getAnalysisId())
                        .build())
                .toList();
    }

}
