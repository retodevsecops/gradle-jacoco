package com.consubanco.model.entities.ocr.util;

import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.vo.OcrUpdateVO;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OcrUpdateVOFactory {

    public static OcrUpdateVO buildFromOcrDocument(OcrDocument ocrDocument) {
        return OcrUpdateVO.builder()
                .id(ocrDocument.getId())
                .data(ocrDocument.getData())
                .status(ocrDocument.getAnalysisResult().getStatus())
                .failureCode(ocrDocument.getAnalysisResult().getFailureCode())
                .failureReason(ocrDocument.getAnalysisResult().getFailureReason())
                .build();
    }
}
