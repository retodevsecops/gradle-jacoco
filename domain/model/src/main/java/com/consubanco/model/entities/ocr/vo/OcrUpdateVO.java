package com.consubanco.model.entities.ocr.vo;

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
}
