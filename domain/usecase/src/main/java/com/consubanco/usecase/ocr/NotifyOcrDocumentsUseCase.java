package com.consubanco.usecase.ocr;

import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.util.FilterListUtil;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import com.consubanco.model.entities.ocr.vo.OcrDocumentSaveVO;
import com.consubanco.model.entities.ocr.vo.OcrDocumentUpdateVO;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.document.BuildAllAgreementDocumentsUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.List;

import static com.consubanco.model.entities.ocr.constant.FailureReason.FAILED_GET_METADATA;
import static com.consubanco.model.entities.ocr.constant.FailureReason.NOT_DATA_EXTRACTED;

@RequiredArgsConstructor
public class NotifyOcrDocumentsUseCase {

    private final OcrDocumentGateway ocrDocumentGateway;
    private final OcrDocumentRepository ocrDocumentRepository;
    private final BuildAllAgreementDocumentsUseCase buildAllAgreementDocumentsUseCase;

    public Mono<List<OcrDocument>> execute(Process process, AgreementConfigVO agreementConfig, List<File> attachments) {
        Mono<Void>  agreementDocuments = buildAllAgreementDocumentsUseCase.execute(process);
        Mono<List<OcrDocument>> ocrDocuments = processOcrDocuments(process, agreementConfig, attachments);
        return Mono.zip(agreementDocuments, ocrDocuments)
                .map(Tuple2::getT2);
    }

    private Mono<List<OcrDocument>> processOcrDocuments(Process process, AgreementConfigVO config, List<File> attachments) {
        return filteredFiles(config, attachments)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(file -> notifyDocument(process, file))
                .sequential()
                .collectList()
                .flatMapMany(ocrDocumentRepository::saveAll)
                .collectList()
                .filter(list -> !list.isEmpty())
                .doOnNext(this::processOcrDocuments)
                .switchIfEmpty(buildAllAgreementDocumentsUseCase.execute(process).thenReturn(List.of()));
    }

    private Flux<File> filteredFiles(AgreementConfigVO agreementConfig, List<File> attachments) {
        List<String> ocrAttachments = agreementConfig.getOcrAttachmentsTechnicalNames();
        List<File> filteredFiles = FilterListUtil.removeCompoundAttachments(attachments);
        return Flux.fromIterable(filteredFiles)
                .filter(file -> ocrAttachments.contains(file.baseFileName()));
    }

    private Mono<OcrDocumentSaveVO> notifyDocument(Process process, File file) {
        OcrDocumentType ocrDocumentType = OcrDocumentType.getTypeFromName(file.getName());
        return ocrDocumentGateway.notifyDocumentForAnalysis(file.getStorageRoute(), ocrDocumentType)
                .map(analysisId -> buildOcrDocumentSave(process, file, analysisId));

    }

    private OcrDocumentSaveVO buildOcrDocumentSave(Process process, File file, String analysisId) {
        return OcrDocumentSaveVO.builder()
                .name(file.getName())
                .storageId(file.getId())
                .storageRoute(file.getStorageRoute())
                .processId(process.getId())
                .analysisId(analysisId)
                .status(OcrStatus.PENDING)
                .build();
    }

    private void processOcrDocuments(List<OcrDocument> ocrDocuments) {
        ocrDocumentGateway.getDelayTime()
                .flatMap(Mono::delay)
                .flatMap(e -> analyzeOcrDocuments(ocrDocuments))
                .subscribeOn(Schedulers.parallel())
                .subscribe();
    }

    private Mono<Void> analyzeOcrDocuments(List<OcrDocument> ocrDocuments) {
        return Flux.fromIterable(ocrDocuments)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::updateOcrDocument)
                .sequential()
                .then();
    }

    private Mono<Void> updateOcrDocument(OcrDocument ocrDocument) {
        return getOcrDocumentStatus(ocrDocument)
                .flatMap(ocrDocumentRepository::update);
    }

    private Mono<OcrDocumentUpdateVO> getOcrDocumentStatus(OcrDocument ocrDocument) {
        return ocrDocumentGateway.getAnalysisData(ocrDocument.getAnalysisId())
                .map(data -> buildUpdateSuccess(ocrDocument, data))
                .defaultIfEmpty(buildUpdateFailed(ocrDocument, NOT_DATA_EXTRACTED.name(), NOT_DATA_EXTRACTED.getMessage()))
                .onErrorResume(error -> Mono.just(buildUpdateFailed(ocrDocument, FAILED_GET_METADATA.name(), error.getMessage())));
    }

    private OcrDocumentUpdateVO buildUpdateSuccess(OcrDocument ocrDocument, List<OcrDataVO> data) {
        return OcrDocumentUpdateVO.builder()
                .id(ocrDocument.getId())
                .status(OcrStatus.SUCCESS)
                .data(data)
                .build();
    }

    private static OcrDocumentUpdateVO buildUpdateFailed(OcrDocument ocrDocument, String code, String reason) {
        return OcrDocumentUpdateVO.builder()
                .id(ocrDocument.getId())
                .status(OcrStatus.FAILED)
                .failureCode(code)
                .failureReason(reason)
                .build();
    }

}