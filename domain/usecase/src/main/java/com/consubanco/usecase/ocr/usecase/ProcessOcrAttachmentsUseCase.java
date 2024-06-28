package com.consubanco.usecase.ocr.usecase;

import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.document.BuildAllAgreementDocumentsUseCase;
import com.consubanco.usecase.ocr.helpers.NotifyOcrDocumentsHelper;
import com.consubanco.usecase.ocr.helpers.ValidateOcrDocumentsHelper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RequiredArgsConstructor
public class ProcessOcrAttachmentsUseCase {

    private final NotifyOcrDocumentsHelper notifyOcrDocuments;
    private final ValidateOcrDocumentsHelper validateOcrDocuments;
    private final BuildAllAgreementDocumentsUseCase buildAllAgreementDocuments;

    public Mono<List<OcrDocument>> execute(Process process, AgreementConfigVO config, List<File> attachments) {
        return notifyOcrDocuments.execute(process, config, attachments)
                .filter(list -> !list.isEmpty())
                .doOnNext(unvalidatedOcrDocuments -> validateOcrDocuments(unvalidatedOcrDocuments, process))
                .switchIfEmpty(generateAgreementDocuments(process));
    }

    private void validateOcrDocuments(List<OcrDocument> unvalidatedOcrDocuments, Process process) {
        validateOcrDocuments.execute(unvalidatedOcrDocuments)
                .filter(this::ocrDocumentsAreValid)
                .flatMap(validatedOcrDocuments -> buildAllAgreementDocuments.execute(process))
                .subscribeOn(Schedulers.parallel())
                .subscribe();
    }

    private boolean ocrDocumentsAreValid(List<OcrDocument> ocrDocuments) {
        return ocrDocuments.parallelStream()
                .allMatch(ocrDocument -> ocrDocument.getStatus().equals(OcrStatus.SUCCESS));
    }

    private Mono<List<OcrDocument>> generateAgreementDocuments(Process process) {
        return buildAllAgreementDocuments.execute(process)
                .thenReturn(List.of());
    }

}
