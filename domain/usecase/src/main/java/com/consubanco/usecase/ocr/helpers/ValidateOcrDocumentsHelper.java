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
import static com.consubanco.model.entities.ocr.util.PeriodicityValidatorUtil.validatePeriodicity;

@RequiredArgsConstructor
public class ValidateOcrDocumentsHelper {

    private final OcrDocumentGateway ocrDocumentGateway;
    private final OcrDocumentRepository ocrDocumentRepository;

    public Mono<List<OcrDocument>> execute(List<OcrDocument> ocrDocuments) {
        return ocrDocumentGateway.getDelayTime()
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
                .flatMap(ocrDocumentRepository::update);
    }

    private Mono<OcrDocumentUpdateVO> getOcrDocumentStatus(OcrDocument ocrDocument) {
        return ocrDocumentGateway.getAnalysisData(ocrDocument.getAnalysisId())
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

    private OcrDocumentUpdateVO checkPayStubs(OcrDocument ocrDocument, List<OcrDataVO> ocrDataList) {
        Integer ocrId = ocrDocument.getId();
        Optional<OcrDataVO> fiscalFolio = OcrDataUtil.getByName(ocrDataList, FISCAL_FOLIO.getKey());
        Optional<OcrDataVO> initialPeriod = OcrDataUtil.getByName(ocrDataList, INITIAL_PERIOD_PAYMENT.getKey());
        Optional<OcrDataVO> finalPeriod = OcrDataUtil.getByName(ocrDataList, FINAL_PERIOD_PAYMENT.getKey());
        if (fiscalFolio.isEmpty()) return new OcrDocumentUpdateVO(ocrId, ocrDataList, FISCAL_FOLIO_NOT_FOUND);
        if (initialPeriod.isEmpty()) return new OcrDocumentUpdateVO(ocrId, ocrDataList, INITIAL_PAY_NOT_FOUND);
        if (finalPeriod.isEmpty()) return new OcrDocumentUpdateVO(ocrId, ocrDataList, FINAL_PAY_NOT_FOUND);
        if (ocrDocument.getDocumentIndex() == -1) return new OcrDocumentUpdateVO(ocrId, ocrDataList, NOT_INDEX);
        return validatePeriodicity(ocrDocument, ocrDataList, initialPeriod.get(), finalPeriod.get(), ocrDocumentGateway.getDaysRangeForPayStubsValidation());
    }

    private OcrDocumentUpdateVO checkProofAddress(OcrDocument ocrDocument, List<OcrDataVO> ocrData) {
        return new OcrDocumentUpdateVO(ocrDocument.getId(), ocrData);
    }

    private OcrDocumentUpdateVO checkIne(OcrDocument ocrDocument, List<OcrDataVO> ocrData) {
        return new OcrDocumentUpdateVO(ocrDocument.getId(), ocrData);
    }

}
