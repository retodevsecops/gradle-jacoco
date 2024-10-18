package com.consubanco.model.entities.ocr.util;

import com.consubanco.model.entities.ocr.OcrAnalysisResult;
import com.consubanco.model.entities.ocr.constant.OcrFailureReason;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OcrResultFactoryUtil {

    public static OcrAnalysisResult analysisSuccess() {
        return OcrAnalysisResult.builder()
                .status(OcrStatus.SUCCESS)
                .build();
    }

    public static OcrAnalysisResult analysisFailed(OcrFailureReason reason) {
        return OcrAnalysisResult.builder()
                .status(OcrStatus.FAILED)
                .failureCode(reason.name())
                .failureReason(reason.getMessage())
                .build();
    }

    public static OcrAnalysisResult analysisFailed(OcrFailureReason reason, Throwable error) {
        return OcrAnalysisResult.builder()
                .status(OcrStatus.FAILED)
                .failureCode(reason.name())
                .failureReason(error.getMessage())
                .build();
    }

    public static OcrAnalysisResult analysisFailed(OcrFailureReason reason, String detail) {
        return OcrAnalysisResult.builder()
                .status(OcrStatus.FAILED)
                .failureCode(reason.name())
                .failureReason(detail)
                .build();
    }

}
