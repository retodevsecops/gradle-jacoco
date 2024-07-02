package com.consubanco.model.entities.ocr.vo;

import com.consubanco.model.entities.ocr.constant.FailureReason;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OcrDocumentUpdateVO {

    private Integer id;
    private OcrStatus status;
    private List<OcrDataVO> data;
    private String failureCode;
    private String failureReason;

    public OcrDocumentUpdateVO(Integer id, List<OcrDataVO> data) {
        this.id = id;
        this.data = data;
        this.status = OcrStatus.SUCCESS;
    }

    public OcrDocumentUpdateVO(Integer id, FailureReason reason) {
        this.id = id;
        this.status = OcrStatus.FAILED;
        this.failureCode = reason.name();
        this.failureReason = reason.getMessage();
    }

    public OcrDocumentUpdateVO(Integer id, FailureReason reason, String detail) {
        this.id = id;
        this.status = OcrStatus.FAILED;
        this.failureCode = reason.name();
        this.failureReason = detail;
    }

    public OcrDocumentUpdateVO(Integer id, List<OcrDataVO> data, FailureReason reason, String detail) {
        this.id = id;
        this.status = OcrStatus.FAILED;
        this.data = data;
        this.failureCode = reason.name();
        this.failureReason = detail;
    }

    public OcrDocumentUpdateVO(Integer id, List<OcrDataVO> data, FailureReason failureReason) {
        this.id = id;
        this.status = OcrStatus.FAILED;
        this.data = data;
        this.failureCode = failureReason.name();
        this.failureReason = failureReason.getMessage();
    }

}
