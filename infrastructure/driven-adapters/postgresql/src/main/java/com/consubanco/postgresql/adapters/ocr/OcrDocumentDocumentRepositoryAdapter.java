package com.consubanco.postgresql.adapters.ocr;

import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
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
import java.util.Objects;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.ocr.message.OcrTechnicalMessage.*;

@Service
@RequiredArgsConstructor
public class OcrDocumentDocumentRepositoryAdapter implements OcrDocumentRepository {

    private final CustomLogger logger;
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
    public Mono<Void> update(OcrDocumentUpdateVO ocrDocumentUpdateVO) {
        return dataRepository.findById(ocrDocumentUpdateVO.getId())
                .flatMap(ocrData -> updateDataDB(ocrDocumentUpdateVO, ocrData))
                .flatMap(dataRepository::save)
                .then()
                .doOnError(error -> logger.error(UPDATE_ERROR.getMessage(), error))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(UPDATE_ERROR));
    }

    @Override
    public Flux<OcrDocument> findByProcessId(String processId) {
        return dataRepository.findByProcessId(processId)
                .flatMap(this::buildOcrDocument)
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(FIND_ERROR));
    }

    private Mono<OcrDocument> buildOcrDocument(OcrDocumentData ocrDocumentData) {
        if (Objects.isNull(ocrDocumentData.getData())) return Mono.just(ocrDocumentData.toEntity());
        return jsonToOcrDataList(ocrDocumentData.getData())
                .map(ocrDocumentData::toEntity);
    }

    private Mono<List<OcrDataVO>> jsonToOcrDataList(Json json) {
        return Mono.fromCallable(() -> mapper.readValue(json.asString(), new TypeReference<List<OcrDataVO>>() {}))
                .onErrorMap(throwTechnicalError(CONVERT_MAP_ERROR));
    }

    private Mono<OcrDocumentData> updateDataDB(OcrDocumentUpdateVO ocrDocumentUpdateVO, OcrDocumentData ocrData) {
        List<OcrDataVO> data = ocrDocumentUpdateVO.getData();
        if(Objects.isNull(data) || data.isEmpty()) return Mono.just(ocrData.update(ocrDocumentUpdateVO));
        return valueToJson(ocrDocumentUpdateVO.getData())
                .map(json -> ocrData.update(ocrDocumentUpdateVO, json));
    }

    private <T> Mono<Json> valueToJson(T value) {
        return Mono.fromCallable(() -> mapper.writeValueAsString(value))
                .map(Json::of)
                .onErrorMap(throwTechnicalError(CONVERT_JSON_ERROR));
    }

}
