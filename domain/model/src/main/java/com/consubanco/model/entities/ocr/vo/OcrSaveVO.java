package com.consubanco.model.entities.ocr.vo;

import com.consubanco.model.entities.ocr.constant.OcrStatus;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OcrSaveVO {
    private String name;
    private String storageId;
    private String storageRoute;
    private String processId;
    private String analysisId;
    private OcrStatus status;
}
