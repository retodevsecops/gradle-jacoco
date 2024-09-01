package com.consubanco.usecase.ocr.usecase;

import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.message.OcrBusinessMessage;
import com.consubanco.model.entities.ocr.message.OcrMessage;
import com.consubanco.model.entities.ocr.vo.OcrResulSetVO;
import com.consubanco.usecase.file.helpers.FileHelper;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildBusiness;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.monoBusiness;
import static com.consubanco.model.entities.ocr.message.OcrBusinessMessage.*;

@RequiredArgsConstructor
public class OcrDocumentUseCase {

    private final FileHelper fileHelper;
    private final OcrDocumentRepository ocrDocumentRepository;
    private final GetProcessByIdUseCase getProcessByIdUseCase;


    public Mono<OcrDocument> findByAnalysisId(String analysisId) {
        return this.validateField(analysisId, ID_ANALYSIS_REQUIRED)
                .flatMap(ocrDocumentRepository::findByAnalysisId)
                .switchIfEmpty(monoBusiness(OCR_DOCUMENT_NOT_FOUND, OcrMessage.notFoundByAnalysisId(analysisId)));
    }

    public Flux<OcrDocument> findByProcessId(String processId) {
        return this.validateField(processId, PROCESS_ID_REQUIRED)
                .flatMapMany(ocrDocumentRepository::findByProcessId)
                .switchIfEmpty(monoBusiness(OCR_DOCUMENT_NOT_FOUND, OcrMessage.notFoundByProcessId(processId)));
    }

    public Mono<OcrDocument> findByStorageId(String storageId) {
        return this.validateField(storageId, STORAGE_ID_REQUIRED)
                .flatMap(ocrDocumentRepository::findByStorageId)
                .switchIfEmpty(monoBusiness(OCR_DOCUMENT_NOT_FOUND, OcrMessage.notFoundByStorageId(storageId)));
    }

    public Mono<OcrResulSetVO> findByDocumentName(String processId, String documentName) {
        return this.getProcessByIdUseCase.execute(processId)
                .flatMap(process -> validateField(documentName, DOCUMENT_NAME_REQUIRED)
                        .map(name -> FileConstants.attachmentRoute(process.getOfferId(), name)))
                .flatMap(fileHelper::findByName)
                .flatMap(file -> this.ocrDocumentRepository.findByStorageId(file.getId())
                        .map(ocrDocument -> new OcrResulSetVO(file, ocrDocument))
                        .switchIfEmpty(Mono.just(new OcrResulSetVO(file))));
    }

    private Mono<String> validateField(String value, OcrBusinessMessage message) {
        return Mono.justOrEmpty(value)
                .switchIfEmpty(buildBusiness(message));
    }

}
