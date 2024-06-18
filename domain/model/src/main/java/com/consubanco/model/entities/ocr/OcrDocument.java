package com.consubanco.model.entities.ocr;

import com.consubanco.model.entities.ocr.constant.OcrStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

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
    private Map<String, Object> data;
    private String detail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
