package com.consubanco.model.entities.ocr;

import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private String detail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
