package com.consubanco.usecase.ocr.helpers;

import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.util.OcrDataUtil;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import com.consubanco.model.entities.ocr.vo.OcrUpdateVO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static com.consubanco.model.entities.ocr.constant.OcrFailureReason.*;
import static com.consubanco.model.entities.ocr.constant.PayStubProperties.*;
import static com.consubanco.model.entities.ocr.constant.ProofAddressProperties.VALIDITY;
import static com.consubanco.model.entities.ocr.constant.ProofAddressProperties.ZIP_CDE;
import static com.consubanco.model.entities.ocr.message.OcrMessage.*;
import static com.consubanco.model.entities.ocr.util.PeriodicityValidatorUtil.validateAddressValidity;
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
                .flatMap(this::getOcrDocumentStatus)
                .flatMap(ocrRepository::update)
                .sequential()
                .collectList();
    }

    private Mono<OcrUpdateVO> getOcrDocumentStatus(OcrDocument ocrDocument) {
        return ocrGateway.getAnalysisData(ocrDocument.getAnalysisId())
                .map(data -> validateOcrDocument(ocrDocument, data))
                .defaultIfEmpty(new OcrUpdateVO(ocrDocument.getId(), NOT_DATA_EXTRACTED))
                .onErrorResume(error -> handleError(ocrDocument, error));
    }

    private OcrUpdateVO validateOcrDocument(OcrDocument ocr, List<OcrDataVO> ocrData) {
        try {
            OcrDocumentType documentType = OcrDocumentType.getTypeFromName(ocr.getBaseName());
            return switch (documentType) {
                case PAY_STUBS -> checkPayStubs(ocr, ocrData);
                case PROOF_ADDRESS -> checkProofAddress(ocr, ocrData);
                case INE -> checkIne(ocr, ocrData);
            };
        } catch (TechnicalException exception) {
            return new OcrUpdateVO(ocr.getId(), ocrData, NOT_PROCESS);
        } catch (DateTimeParseException exception) {
            return new OcrUpdateVO(ocr.getId(), ocrData, INVALID_DATE_FORMAT, exception.getMessage());
        } catch (Exception exception) {
            return new OcrUpdateVO(ocr.getId(), ocrData, UNKNOWN_ERROR, exception.getMessage());
        }
    }

    private OcrUpdateVO checkPayStubs(OcrDocument ocrDocument, List<OcrDataVO> ocrData) {
        Integer ocrId = ocrDocument.getId();
        Optional<OcrUpdateVO> checkFiscalFolio = checkFiscalFolio(ocrData, ocrId);
        if (checkFiscalFolio.isPresent()) return checkFiscalFolio.get();
        Optional<OcrDataVO> initialPeriod = OcrDataUtil.getByName(ocrData, INITIAL_PERIOD_PAYMENT.getKey());
        Optional<OcrDataVO> finalPeriod = OcrDataUtil.getByName(ocrData, FINAL_PERIOD_PAYMENT.getKey());
        if (initialPeriod.isEmpty()) return new OcrUpdateVO(ocrId, ocrData, INITIAL_PAY_NOT_FOUND);
        if (finalPeriod.isEmpty()) return new OcrUpdateVO(ocrId, ocrData, FINAL_PAY_NOT_FOUND);
        if (ocrDocument.getDocumentIndex() == -1) return new OcrUpdateVO(ocrId, ocrData, NOT_INDEX);
        return validatePeriodicity(ocrDocument, ocrData, initialPeriod.get(), finalPeriod.get(), ocrGateway.getDaysRangeForPayStubsValidation());
    }

    private OcrUpdateVO checkProofAddress(OcrDocument ocrDocument, List<OcrDataVO> ocrData) {
        Optional<OcrUpdateVO> updateVO = checkSingleData(ZIP_CDE.getKey(), ocrData, ocrDocument.getId());
        if(updateVO.isPresent()) return updateVO.get();
        Optional<OcrUpdateVO> validityConfidence = checkSingleData(VALIDITY.getKey(), ocrData, ocrDocument.getId());
        if(validityConfidence.isPresent()) return validityConfidence.get();
        Optional<OcrDataVO> validity = OcrDataUtil.getByName(ocrData, VALIDITY.getKey());
        if (validity.isEmpty()) return new OcrUpdateVO(ocrDocument.getId(), ocrData, ADDRESS_VALIDITY_NOT_FOUND);
        return validateAddressValidity(ocrDocument, ocrData, validity.get(), 3);
    }

    private OcrUpdateVO checkIne(OcrDocument ocrDocument, List<OcrDataVO> ocrData) {
        return new OcrUpdateVO(ocrDocument.getId(), ocrData);
    }

    private Optional<OcrUpdateVO> checkFiscalFolio(List<OcrDataVO> ocrData, int ocrId) {
        Optional<OcrUpdateVO> ocrDocumentUpdate = checkSingleData(FISCAL_FOLIO.getKey(), ocrData, ocrId);
        if (ocrDocumentUpdate.isPresent()) return ocrDocumentUpdate;
        String fiscalFolio = extractFiscalFolio(ocrData);
        if (fiscalFolio.length() != 36) {
            String reason = invalidFiscalFolio(fiscalFolio);
            return Optional.of(new OcrUpdateVO(ocrId, ocrData, INVALID_FISCAL_FOLIO, reason));
        }
        return Optional.empty();
    }

    private Optional<OcrUpdateVO> checkSingleData(String dataName, List<OcrDataVO> ocrData, int ocrId) {
        Optional<OcrDataVO> data = OcrDataUtil.getByName(ocrData, dataName);
        if (data.isEmpty()) {
            var ocrUpdate = new OcrUpdateVO(ocrId, ocrData, DATA_NOT_FOUND, dataNotFound(dataName));
            return Optional.of(ocrUpdate);
        }
        if (data.get().getConfidence() < ocrGateway.getConfidence()) {
            String reason = invalidConfidence(dataName, data.get().getConfidence(), ocrGateway.getConfidence());
            var ocrUpdate = new OcrUpdateVO(ocrId, ocrData, INVALID_CONFIDENCE, reason);
            return Optional.of(ocrUpdate);
        }
        return Optional.empty();
    }

    private String extractFiscalFolio(List<OcrDataVO> ocrData) {
        return OcrDataUtil.getByName(ocrData, FISCAL_FOLIO.getKey())
                .map(dataVO -> dataVO.getValue().trim())
                .orElse("");
    }

    private Mono<OcrUpdateVO> handleError(OcrDocument ocrDocument, Throwable error) {
        var ocrUpdate = new OcrUpdateVO(ocrDocument.getId(), FAILED_GET_METADATA, error.getMessage());
        return Mono.just(ocrUpdate);
    }

}
