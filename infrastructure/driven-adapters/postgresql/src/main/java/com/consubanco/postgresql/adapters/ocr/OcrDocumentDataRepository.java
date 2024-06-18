package com.consubanco.postgresql.adapters.ocr;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OcrDocumentDataRepository extends ReactiveCrudRepository<OcrDocumentData, Integer> {
    @Query("SELECT * FROM ocr_document WHERE process_id = :processId")
    Flux<OcrDocumentData> findByProcessId(@Param("processId") String processId);
}