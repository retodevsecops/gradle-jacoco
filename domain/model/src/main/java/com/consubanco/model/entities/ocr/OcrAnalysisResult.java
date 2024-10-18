package com.consubanco.model.entities.ocr;

import com.consubanco.model.entities.ocr.constant.OcrStatus;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OcrAnalysisResult {
    private OcrStatus status;
    private String failureCode;
    private String failureReason;
}
