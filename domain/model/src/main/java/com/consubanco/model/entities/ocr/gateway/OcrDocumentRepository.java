package com.consubanco.model.entities.ocr.gateway;

import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.vo.OcrSaveVO;
import com.consubanco.model.entities.ocr.vo.OcrUpdateVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OcrDocumentRepository {
    Mono<OcrDocument> save(OcrSaveVO ocrSaveVO);
    Flux<OcrDocument> saveAll(List<OcrSaveVO> ocrSaveVOList);
    Mono<OcrDocument> update(OcrUpdateVO ocrUpdateVO);
    Flux<OcrDocument> findByProcessId(String processId);
    Mono<OcrDocument> findByAnalysisId(String processId);
    Mono<OcrDocument> findByStorageId(String storageId);
}
