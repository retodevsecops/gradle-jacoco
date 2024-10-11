package com.consubanco.model.entities.ocr.util;

import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;

import static com.consubanco.model.entities.ocr.constant.PayStubProperties.FINAL_PERIOD_PAYMENT;
import static com.consubanco.model.entities.ocr.constant.PayStubProperties.INITIAL_PERIOD_PAYMENT;

@UtilityClass
public class OcrDocumentsValidatorUtil {

    public static boolean isDuplicatePayStub(OcrDocument ocrDocument, List<OcrDocument> allDocuments) {
        String initialPeriod = ocrDocument.getDataByName(INITIAL_PERIOD_PAYMENT.getKey())
                .map(OcrDataVO::getValue)
                .orElse(null);
        String finalPeriod = ocrDocument.getDataByName(FINAL_PERIOD_PAYMENT.getKey())
                .map(OcrDataVO::getValue)
                .orElse(null);
        return allDocuments.stream()
                .filter(ocrDoc -> !Objects.equals(ocrDoc.getId(), ocrDocument.getId()) && ocrDoc.isPayStub())
                .anyMatch(ocrDoc -> {
                    String initial = ocrDoc.getDataByName(INITIAL_PERIOD_PAYMENT.getKey())
                            .map(OcrDataVO::getValue)
                            .orElse(null);
                    String finalPeriodDoc = ocrDoc.getDataByName(FINAL_PERIOD_PAYMENT.getKey())
                            .map(OcrDataVO::getValue)
                            .orElse(null);
                    return Objects.equals(initialPeriod, initial) && Objects.equals(finalPeriod, finalPeriodDoc);
                });
    }

}