package com.consubanco.postgresql.adapters.ocr;

import com.consubanco.model.entities.ocr.OcrAnalysisResult;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import com.consubanco.model.entities.ocr.vo.OcrSaveVO;
import com.consubanco.model.entities.ocr.vo.OcrUpdateVO;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ocr_document")
public class OcrDocumentData {

    @Id
    private Integer id;
    private String name;
    private OcrStatus status;
    private Json data;

    @Column("failure_code")
    private String failureCode;

    @Column("failure_reason")
    private String failureReason;

    @Column("storage_id")
    private String storageId;

    @Column("storage_route")
    private String storageRoute;

    @Column("process_id")
    private String processId;

    @Column("analysis_id")
    private String analysisId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    public OcrDocumentData(OcrSaveVO ocrSaveVO) {
        this.name = ocrSaveVO.getName();
        this.storageId = ocrSaveVO.getStorageId();
        this.storageRoute = ocrSaveVO.getStorageRoute();
        this.processId = ocrSaveVO.getProcessId();
        this.analysisId = ocrSaveVO.getAnalysisId();
        this.status = ocrSaveVO.getStatus();
    }

    public OcrDocument toEntity(List<OcrDataVO> data) {
        return OcrDocument.builder()
                .id(this.id)
                .name(this.name)
                .storageId(this.storageId)
                .storageRoute(this.storageRoute)
                .processId(this.processId)
                .analysisId(this.analysisId)
                .analysisResult(OcrAnalysisResult.builder()
                        .status(this.status)
                        .failureCode(this.failureCode)
                        .failureReason(this.failureReason)
                        .build())
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .data(data)
                .build();
    }

    public OcrDocument toEntity() {
        return this.toEntity(null);
    }

    public OcrDocumentData update(OcrUpdateVO ocrUpdateVO, Json data) {
        this.data = data;
        return update(ocrUpdateVO);
    }

    public OcrDocumentData update(OcrUpdateVO ocrUpdateVO) {
        this.status = ocrUpdateVO.getStatus();
        this.failureCode = ocrUpdateVO.getFailureCode();
        this.failureReason = ocrUpdateVO.getFailureReason();
        this.updatedAt = LocalDateTime.now();
        return this;
    }

}
