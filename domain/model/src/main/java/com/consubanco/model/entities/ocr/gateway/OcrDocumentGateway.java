package com.consubanco.model.entities.ocr.gateway;

import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface OcrDocumentGateway {
    Mono<String> notifyDocumentForAnalysis(String storageRoute, OcrDocumentType ocrDocumentType);
    Mono<Map<String, Object>> getAnalysisData(String analysisId);
}
