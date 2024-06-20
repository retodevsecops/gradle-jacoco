package com.consubanco.model.entities.ocr.gateway;

import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.vo.OcrDocumentSaveVO;
import com.consubanco.model.entities.ocr.vo.OcrDocumentUpdateVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OcrDocumentRepository {
    Flux<OcrDocument> saveAll(List<OcrDocumentSaveVO> ocrDocumentSaveVOList);
    Mono<Void> update(OcrDocumentUpdateVO ocrDocumentUpdateVO);
    Flux<OcrDocument> findByProcessId(String processId);
}
