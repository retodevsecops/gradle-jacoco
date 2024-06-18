package com.consubanco.postgresql.adapters.ocr;

import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.vo.OcrDocumentSaveVO;
import com.consubanco.model.entities.ocr.vo.OcrDocumentUpdateVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.ocr.message.OcrTechnicalMessage.*;

@Service
@RequiredArgsConstructor
public class OcrDocumentDocumentRepositoryAdapter implements OcrDocumentRepository {

    private final OcrDocumentDataRepository dataRepository;
    private final ObjectMapper mapper;

    @Override
    public Flux<OcrDocument> saveAll(List<OcrDocumentSaveVO> ocrDocumentSaveVOList) {
        return Flux.fromIterable(ocrDocumentSaveVOList)
                .map(OcrDocumentData::new)
                .collectList()
                .flatMapMany(dataRepository::saveAll)
                .flatMap(this::buildOcrDocument)
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(SAVE_ALL_ERROR));

    }

    @Override
    public Mono<OcrDocument> update(OcrDocumentUpdateVO ocrDocumentUpdateVO) {
        return null;
    }

    @Override
    public Flux<OcrDocument> findByProcessId(String processId) {
        return null;
    }

    private Mono<OcrDocument> buildOcrDocument(OcrDocumentData data) {
        if (Objects.isNull(data.getData())) return Mono.just(data.toEntity());
        return jsonToMap(data.getData())
                .map(data::toEntity);
    }

    private <T> Mono<Json> valueToJson(T value) {
        return Mono.fromCallable(() -> mapper.writeValueAsString(value))
                .map(Json::of)
                .onErrorMap(throwTechnicalError(CONVERT_JSON_ERROR));
    }

    private Mono<Map<String, Object>> jsonToMap(Json json) {
        return Mono.fromCallable(() -> mapper.readValue(json.asString(), new TypeReference<Map<String, Object>>() {}))
                .onErrorMap(throwTechnicalError(CONVERT_MAP_ERROR));
    }

}
