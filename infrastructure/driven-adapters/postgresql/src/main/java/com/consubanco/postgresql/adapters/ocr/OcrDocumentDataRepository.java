package com.consubanco.postgresql.adapters.ocr;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OcrDocumentDataRepository extends ReactiveCrudRepository<OcrDocumentData, Integer> {
    @Query("SELECT * FROM ocr_document WHERE process_id = :processId")
    Flux<OcrDocumentData> findByProcessId(@Param("processId") String processId);

    @Query("SELECT * FROM ocr_document WHERE analysis_id = :analysisId")
    Mono<OcrDocumentData> findByAnalysisId(@Param("analysisId") String analysisId);

    @Query("SELECT * FROM ocr_document WHERE storage_id = :storageId")
    Mono<OcrDocumentData> findByStorageId(@Param("storageId") String storageId);

}