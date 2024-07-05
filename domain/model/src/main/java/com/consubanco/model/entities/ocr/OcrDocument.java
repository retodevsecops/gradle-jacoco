package com.consubanco.model.entities.ocr;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.consubanco.model.entities.ocr.message.OcrBusinessMessage.OCR_INVALID;
import static com.consubanco.model.entities.ocr.message.OcrMessage.ocrInvalid;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OcrDocument {

    private Integer id;
    private String name;
    private String storageId;
    private String storageRoute;
    private String processId;
    private String analysisId;
    private OcrStatus status;
    private List<OcrDataVO> data;
    private String failureCode;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getBaseName() {
        return name.replaceAll("-\\d+$", "");
    }

    public int getDocumentIndex() {
        if (name == null || !name.matches(".*-\\d+$")) return -1;
        String indexStr = name.replaceAll("^.*-(\\d+)$", "$1");
        return Integer.parseInt(indexStr);
    }

    public OcrDocument checkSuccessStatus() {
        if (status.equals(OcrStatus.SUCCESS)) return this;
        String cause = ocrInvalid(name, analysisId, status);
        throw ExceptionFactory.buildBusiness(cause, OCR_INVALID);
    }

}