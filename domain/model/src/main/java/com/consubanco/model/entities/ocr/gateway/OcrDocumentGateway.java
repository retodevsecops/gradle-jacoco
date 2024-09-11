package com.consubanco.model.entities.ocr.gateway;

import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public interface OcrDocumentGateway {
    Double getConfidence();
    Integer getDaysRangeForPayStubsValidation();
    Mono<Duration> getDelayTime();
    Mono<String> notifyDocumentForAnalysis(String storageRoute, OcrDocumentType ocrDocumentType);
    Mono<List<OcrDataVO>> getAnalysisData(String analysisId);
}
