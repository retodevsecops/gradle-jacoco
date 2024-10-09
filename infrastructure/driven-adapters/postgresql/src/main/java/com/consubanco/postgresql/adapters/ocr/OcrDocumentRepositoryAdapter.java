package com.consubanco.postgresql.adapters.ocr;

import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import com.consubanco.model.entities.ocr.vo.OcrSaveVO;
import com.consubanco.model.entities.ocr.vo.OcrUpdateVO;
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
public class OcrDocumentRepositoryAdapter implements OcrDocumentRepository {

    private final CustomLogger logger;
    private final OcrDocumentDataRepository dataRepository;
    private final ObjectMapper mapper;

    @Override
    public Mono<OcrDocument> save(OcrSaveVO ocrSaveVO) {
        OcrDocumentData ocrDocumentData = new OcrDocumentData(ocrSaveVO);
        return dataRepository.save(ocrDocumentData)
                .flatMap(this::buildOcrDocument)
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(SAVE_ERROR));
    }

    @Override
    public Flux<OcrDocument> saveAll(List<OcrSaveVO> ocrSaveVOList) {
        return Flux.fromIterable(ocrSaveVOList)
                .map(OcrDocumentData::new)
                .collectList()
                .flatMapMany(dataRepository::saveAll)
                .flatMap(this::buildOcrDocument)
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(SAVE_ALL_ERROR));
    }

    @Override
    public Mono<OcrDocument> update(OcrUpdateVO ocrUpdateVO) {
        return dataRepository.findById(ocrUpdateVO.getId())
                .flatMap(ocrData -> updateDataDB(ocrUpdateVO, ocrData))
                .flatMap(dataRepository::save)
                .flatMap(this::buildOcrDocument)
                .doOnError(error -> logger.error(UPDATE_ERROR.getMessage(), error))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(UPDATE_ERROR));
    }

    @Override
    public Flux<OcrDocument> findByProcessId(String processId) {
        return dataRepository.findByProcessId(processId)
                .flatMap(this::buildOcrDocument)
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(FIND_ERROR));
    }

    @Override
    public Mono<OcrDocument> findByAnalysisId(String analysisId) {
        return dataRepository.findByAnalysisId(analysisId)
                .flatMap(this::buildOcrDocument)
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(FIND_ERROR));
    }

    @Override
    public Mono<OcrDocument> findByStorageId(String storageId) {
        return dataRepository.findByStorageId(storageId)
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

    private Mono<OcrDocumentData> updateDataDB(OcrUpdateVO ocrUpdateVO, OcrDocumentData ocrData) {
        List<OcrDataVO> data = ocrUpdateVO.getData();
        if(Objects.isNull(data) || data.isEmpty()) return Mono.just(ocrData.update(ocrUpdateVO));
        return valueToJson(ocrUpdateVO.getData())
                .map(json -> ocrData.update(ocrUpdateVO, json));
    }

    private <T> Mono<Json> valueToJson(T value) {
        return Mono.fromCallable(() -> mapper.writeValueAsString(value))
                .map(Json::of)
                .onErrorMap(throwTechnicalError(CONVERT_JSON_ERROR));
    }

}
