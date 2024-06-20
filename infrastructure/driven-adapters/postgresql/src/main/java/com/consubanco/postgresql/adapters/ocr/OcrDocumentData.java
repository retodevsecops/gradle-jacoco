package com.consubanco.postgresql.adapters.ocr;

import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.consubanco.model.entities.ocr.vo.OcrDocumentSaveVO;
import com.consubanco.model.entities.ocr.vo.OcrDocumentUpdateVO;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Map;

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
    private String detail;

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

    public OcrDocumentData(OcrDocumentSaveVO ocrDocumentSaveVO) {
        this.name = ocrDocumentSaveVO.getName();
        this.storageId = ocrDocumentSaveVO.getStorageId();
        this.storageRoute = ocrDocumentSaveVO.getStorageRoute();
        this.processId = ocrDocumentSaveVO.getProcessId();
        this.analysisId = ocrDocumentSaveVO.getAnalysisId();
        this.status = ocrDocumentSaveVO.getStatus();
    }

    public OcrDocument toEntity(Map<String, Object> data) {
        return OcrDocument.builder()
                .id(this.id)
                .name(this.name)
                .storageId(this.storageId)
                .storageRoute(this.storageRoute)
                .processId(this.processId)
                .analysisId(this.analysisId)
                .status(this.status)
                .detail(this.detail)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .data(data)
                .build();
    }

    public OcrDocument toEntity() {
        return this.toEntity(null);
    }

    public OcrDocumentData update(OcrDocumentUpdateVO ocrDocumentUpdateVO, Json data) {
        this.data = data;
        return update(ocrDocumentUpdateVO);
    }

    public OcrDocumentData update(OcrDocumentUpdateVO ocrDocumentUpdateVO) {
        this.detail = ocrDocumentUpdateVO.getDetail();
        this.status = ocrDocumentUpdateVO.getStatus();
        return this;
    }

}
