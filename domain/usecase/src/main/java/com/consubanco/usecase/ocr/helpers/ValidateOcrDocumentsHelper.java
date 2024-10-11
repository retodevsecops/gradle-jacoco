package com.consubanco.usecase.ocr.helpers;

import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.ocr.OcrAnalysisResult;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.constant.OcrFailureReason;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.util.OcrUpdateVOFactory;
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
import static com.consubanco.model.entities.ocr.constant.PayStubProperties.FINAL_PERIOD_PAYMENT;
import static com.consubanco.model.entities.ocr.constant.PayStubProperties.INITIAL_PERIOD_PAYMENT;
import static com.consubanco.model.entities.ocr.constant.ProofAddressProperties.VALIDITY;
import static com.consubanco.model.entities.ocr.constant.ProofAddressProperties.ZIP_CDE;
import static com.consubanco.model.entities.ocr.message.OcrMessage.dataNotFound;
import static com.consubanco.model.entities.ocr.message.OcrMessage.invalidConfidence;
import static com.consubanco.model.entities.ocr.util.OcrDocumentsValidatorUtil.isDuplicatePayStub;
import static com.consubanco.model.entities.ocr.util.OcrResultFactoryUtil.analysisFailed;
import static com.consubanco.model.entities.ocr.util.OcrResultFactoryUtil.analysisSuccess;
import static com.consubanco.model.entities.ocr.util.PeriodicityValidatorUtil.validateAddressValidity;
import static com.consubanco.model.entities.ocr.util.PeriodicityValidatorUtil.validatePeriodicity;

@RequiredArgsConstructor
public class ValidateOcrDocumentsHelper {

    private final OcrDocumentGateway ocrGateway;
    private final OcrDocumentRepository ocrRepository;

    public Mono<List<OcrDocument>> execute(List<OcrDocument> ocrDocuments) {
        return ocrGateway.getDelayTime()
                .flatMap(Mono::delay)
                .flatMap(e -> enrichDocumentsWithData(ocrDocuments))
                .flatMap(this::performDocumentValidation);
    }

    private Mono<List<OcrDocument>> enrichDocumentsWithData(List<OcrDocument> ocrDocuments) {
        return Flux.fromIterable(ocrDocuments)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::fetchAnalysisData)
                .sequential()
                .collectList();
    }

    private Mono<OcrDocument> fetchAnalysisData(OcrDocument ocrDocument) {
        return ocrGateway.getAnalysisData(ocrDocument.getAnalysisId())
                .map(analysisData -> ocrDocument.toBuilder().data(analysisData).build())
                .defaultIfEmpty(ocrDocument.toBuilder()
                        .analysisResult(analysisFailed(NOT_DATA_EXTRACTED))
                        .build())
                .onErrorResume(error -> handleError(ocrDocument, FAILED_GET_METADATA, error));
    }

    private Mono<List<OcrDocument>> performDocumentValidation(List<OcrDocument> ocrDocuments) {
        return Flux.fromIterable(ocrDocuments)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(ocrDocument -> validateAndUpdateDocument(ocrDocument, ocrDocuments))
                .flatMap(ocrRepository::update)
                .sequential()
                .collectList();
    }

    private OcrUpdateVO validateAndUpdateDocument(OcrDocument ocrDocument, List<OcrDocument> ocrDocuments) {
        if (ocrDocument.isAlreadyValidated()) return OcrUpdateVOFactory.buildFromOcrDocument(ocrDocument);
        OcrAnalysisResult analysisResult = validateOcrDocumentType(ocrDocument, ocrDocuments);
        return OcrUpdateVOFactory.buildFromOcrDocument(ocrDocument.toBuilder()
                .analysisResult(analysisResult)
                .build());
    }

    private OcrAnalysisResult validateOcrDocumentType(OcrDocument ocrDocument, List<OcrDocument> allDocuments) {
        try {
            OcrDocumentType documentType = OcrDocumentType.getTypeFromName(ocrDocument.getBaseName());
            return switch (documentType) {
                case PAY_STUBS -> validatePayStubDocument(ocrDocument, allDocuments);
                case PROOF_ADDRESS -> validateProofOfAddressDocument(ocrDocument);
                case INE -> analysisSuccess();
            };
        } catch (TechnicalException exception) {
            return analysisFailed(NOT_PROCESS, exception);
        } catch (DateTimeParseException exception) {
            return analysisFailed(INVALID_DATE_FORMAT, exception);
        } catch (Exception exception) {
            return analysisFailed(UNKNOWN_ERROR, exception);
        }
    }

    private OcrAnalysisResult validatePayStubDocument(OcrDocument ocrDocument, List<OcrDocument> allDocuments) {
        Optional<OcrDataVO> initialPeriod = ocrDocument.getDataByName(INITIAL_PERIOD_PAYMENT.getKey());
        Optional<OcrDataVO> finalPeriod = ocrDocument.getDataByName(FINAL_PERIOD_PAYMENT.getKey());
        if (initialPeriod.isEmpty()) return analysisFailed(INITIAL_PAY_NOT_FOUND);
        if (finalPeriod.isEmpty()) return analysisFailed(FINAL_PAY_NOT_FOUND);
        if (ocrDocument.getDocumentIndex() == -1) return analysisFailed(NOT_INDEX);
        if(isDuplicatePayStub(ocrDocument, allDocuments)) return analysisFailed(DUPLICATE_PAY_STUB);
        return validatePeriodicity(ocrDocument.getDocumentIndex(),
                initialPeriod.get().getValue(),
                finalPeriod.get().getValue(),
                ocrGateway.getDaysRangeForPayStubsValidation());
    }

    private OcrAnalysisResult validateProofOfAddressDocument(OcrDocument ocrDocument) {
        Optional<OcrAnalysisResult> zipCodeResult = validateDataPresence(ZIP_CDE.getKey(), ocrDocument);
        if (zipCodeResult.isPresent()) return zipCodeResult.get();
        Optional<OcrAnalysisResult> validityResult = validateDataPresence(VALIDITY.getKey(), ocrDocument);
        if (validityResult.isPresent()) return validityResult.get();
        Optional<OcrDataVO> validity = ocrDocument.getDataByName(VALIDITY.getKey());
        if (validity.isEmpty()) return analysisFailed(ADDRESS_VALIDITY_NOT_FOUND);
        return validateAddressValidity(validity.get().getValue(), 3);
    }

    private Optional<OcrAnalysisResult> validateDataPresence(String dataName, OcrDocument ocrDocument) {
        Optional<OcrDataVO> data = ocrDocument.getDataByName(dataName);
        if (data.isEmpty()) return Optional.of(analysisFailed(DATA_NOT_FOUND, dataNotFound(dataName)));
        if (data.get().getConfidence() < ocrGateway.getConfidence()) {
            String reason = invalidConfidence(dataName, data.get().getConfidence(), ocrGateway.getConfidence());
            return Optional.of(analysisFailed(INVALID_CONFIDENCE, reason));
        }
        return Optional.empty();
    }

    private Mono<OcrDocument> handleError(OcrDocument ocrDocument, OcrFailureReason reason, Throwable error) {
        return Mono.just(ocrDocument.toBuilder()
                .analysisResult(analysisFailed(reason, error))
                .build());
    }

}
