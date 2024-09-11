package com.consubanco.usecase.ocr.helpers;

import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.util.OcrDataUtil;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import com.consubanco.model.entities.ocr.vo.OcrDocumentUpdateVO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static com.consubanco.model.entities.ocr.constant.FailureReason.*;
import static com.consubanco.model.entities.ocr.constant.PayStubProperties.*;
import static com.consubanco.model.entities.ocr.constant.ProofAddressProperties.ZIP_CDE;
import static com.consubanco.model.entities.ocr.message.OcrMessage.dataNotFound;
import static com.consubanco.model.entities.ocr.message.OcrMessage.invalidConfidence;
import static com.consubanco.model.entities.ocr.util.PeriodicityValidatorUtil.validatePeriodicity;

@RequiredArgsConstructor
public class ValidateOcrDocumentsHelper {

    private final OcrDocumentGateway ocrGateway;
    private final OcrDocumentRepository ocrRepository;

    public Mono<List<OcrDocument>> execute(List<OcrDocument> ocrDocuments) {
        return ocrGateway.getDelayTime()
                .flatMap(Mono::delay)
                .flatMap(e -> analyzeOcrDocuments(ocrDocuments));
    }

    private Mono<List<OcrDocument>> analyzeOcrDocuments(List<OcrDocument> ocrDocuments) {
        return Flux.fromIterable(ocrDocuments)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::updateOcrDocument)
                .sequential()
                .collectList();
    }

    private Mono<OcrDocument> updateOcrDocument(OcrDocument ocrDocument) {
        return getOcrDocumentStatus(ocrDocument)
                .flatMap(ocrRepository::update);
    }

    private Mono<OcrDocumentUpdateVO> getOcrDocumentStatus(OcrDocument ocrDocument) {
        return ocrGateway.getAnalysisData(ocrDocument.getAnalysisId())
                .map(data -> validateOcrDocument(ocrDocument, data))
                .defaultIfEmpty(new OcrDocumentUpdateVO(ocrDocument.getId(), NOT_DATA_EXTRACTED))
                .onErrorResume(error -> Mono.just(new OcrDocumentUpdateVO(ocrDocument.getId(), FAILED_GET_METADATA, error.getMessage())));
    }

    private OcrDocumentUpdateVO validateOcrDocument(OcrDocument ocr, List<OcrDataVO> ocrData) {
        try {
            OcrDocumentType documentType = OcrDocumentType.getTypeFromName(ocr.getBaseName());
            return switch (documentType) {
                case PAY_STUBS -> checkPayStubs(ocr, ocrData);
                case PROOF_ADDRESS -> checkProofAddress(ocr, ocrData);
                case INE -> checkIne(ocr, ocrData);
            };
        } catch (TechnicalException exception) {
            return new OcrDocumentUpdateVO(ocr.getId(), ocrData, NOT_PROCESS);
        } catch (DateTimeParseException exception) {
            return new OcrDocumentUpdateVO(ocr.getId(), ocrData, INVALID_DATE_FORMAT, exception.getMessage());
        } catch (Exception exception) {
            return new OcrDocumentUpdateVO(ocr.getId(), ocrData, UNKNOWN_ERROR, exception.getMessage());
        }
    }

    private OcrDocumentUpdateVO checkPayStubs(OcrDocument ocrDocument, List<OcrDataVO> ocrData) {
        Integer ocrId = ocrDocument.getId();
        Optional<OcrDocumentUpdateVO> fiscalFolio = checkSingleData(FISCAL_FOLIO.getKey(), ocrData, ocrId);
        if (fiscalFolio.isPresent()) return fiscalFolio.get();
        Optional<OcrDataVO> initialPeriod = OcrDataUtil.getByName(ocrData, INITIAL_PERIOD_PAYMENT.getKey());
        Optional<OcrDataVO> finalPeriod = OcrDataUtil.getByName(ocrData, FINAL_PERIOD_PAYMENT.getKey());
        if (initialPeriod.isEmpty()) return new OcrDocumentUpdateVO(ocrId, ocrData, INITIAL_PAY_NOT_FOUND);
        if (finalPeriod.isEmpty()) return new OcrDocumentUpdateVO(ocrId, ocrData, FINAL_PAY_NOT_FOUND);
        if (ocrDocument.getDocumentIndex() == -1) return new OcrDocumentUpdateVO(ocrId, ocrData, NOT_INDEX);
        return validatePeriodicity(ocrDocument, ocrData, initialPeriod.get(), finalPeriod.get(), ocrGateway.getDaysRangeForPayStubsValidation());
    }

    private OcrDocumentUpdateVO checkProofAddress(OcrDocument ocrDocument, List<OcrDataVO> ocrData) {
        Optional<OcrDocumentUpdateVO> updateVO = checkSingleData(ZIP_CDE.getKey(), ocrData, ocrDocument.getId());
        return updateVO.orElseGet(() -> new OcrDocumentUpdateVO(ocrDocument.getId(), ocrData));
    }

    private OcrDocumentUpdateVO checkIne(OcrDocument ocrDocument, List<OcrDataVO> ocrData) {
        return new OcrDocumentUpdateVO(ocrDocument.getId(), ocrData);
    }

    private Optional<OcrDocumentUpdateVO> checkSingleData(String dataName, List<OcrDataVO> ocrData, int ocrId) {
        Optional<OcrDataVO> data = OcrDataUtil.getByName(ocrData, dataName);
        if (data.isEmpty()) return Optional.of(new OcrDocumentUpdateVO(ocrId, ocrData, DATA_NOT_FOUND, dataNotFound(dataName)));
        if (data.get().getConfidence() < ocrGateway.getConfidence()) {
            String reason = invalidConfidence(dataName, data.get().getConfidence(), ocrGateway.getConfidence());
            return Optional.of(new OcrDocumentUpdateVO(ocrId, ocrData, INVALID_CONFIDENCE, reason));
        }
        return Optional.empty();
    }

}
