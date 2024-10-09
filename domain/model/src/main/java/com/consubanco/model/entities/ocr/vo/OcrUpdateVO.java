package com.consubanco.model.entities.ocr.vo;

import com.consubanco.model.entities.ocr.constant.OcrFailureReason;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OcrUpdateVO {

    private Integer id;
    private OcrStatus status;
    private List<OcrDataVO> data;
    private String failureCode;
    private String failureReason;

    public OcrUpdateVO(Integer id, List<OcrDataVO> data) {
        this.id = id;
        this.data = data;
        this.status = OcrStatus.SUCCESS;
    }

    public OcrUpdateVO(Integer id, OcrFailureReason reason) {
        this.id = id;
        this.status = OcrStatus.FAILED;
        this.failureCode = reason.name();
        this.failureReason = reason.getMessage();
    }

    public OcrUpdateVO(Integer id, OcrFailureReason reason, String detail) {
        this.id = id;
        this.status = OcrStatus.FAILED;
        this.failureCode = reason.name();
        this.failureReason = detail;
    }

    public OcrUpdateVO(Integer id, List<OcrDataVO> data, OcrFailureReason reason, String detail) {
        this.id = id;
        this.status = OcrStatus.FAILED;
        this.data = data;
        this.failureCode = reason.name();
        this.failureReason = detail;
    }

    public OcrUpdateVO(Integer id, List<OcrDataVO> data, OcrFailureReason ocrFailureReason) {
        this.id = id;
        this.status = OcrStatus.FAILED;
        this.data = data;
        this.failureCode = ocrFailureReason.name();
        this.failureReason = ocrFailureReason.getMessage();
    }

}
