package com.consubanco.usecase.ocr.helpers;

import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.util.FilterListUtil;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.ocr.vo.OcrSaveVO;
import com.consubanco.model.entities.process.Process;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RequiredArgsConstructor
public class NotifyOcrDocumentsHelper {

    private final OcrDocumentGateway ocrDocumentGateway;
    private final OcrDocumentRepository ocrDocumentRepository;

    public Mono<List<OcrDocument>> execute(Process process, AgreementConfigVO config, List<File> attachments) {
        return filteredFiles(config, attachments)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(file -> notifyDocument(process, file))
                .sequential()
                .collectList()
                .flatMapMany(ocrDocumentRepository::saveAll)
                .collectList();
    }

    private Flux<File> filteredFiles(AgreementConfigVO agreementConfig, List<File> attachments) {
        List<String> ocrAttachments = agreementConfig.getOcrAttachmentsTechnicalNames();
        List<File> filteredFiles = FilterListUtil.removeCompoundAttachments(attachments);
        return Flux.fromIterable(filteredFiles)
                .filter(file -> ocrAttachments.contains(file.baseFileName()));
    }

    private Mono<OcrSaveVO> notifyDocument(Process process, File file) {
        OcrDocumentType ocrDocumentType = OcrDocumentType.getTypeFromName(file.getName());
        return ocrDocumentGateway.notifyDocumentForAnalysis(file.getStorageRoute(), ocrDocumentType)
                .map(analysisId -> buildOcrDocumentSave(process, file, analysisId));

    }

    private OcrSaveVO buildOcrDocumentSave(Process process, File file, String analysisId) {
        return OcrSaveVO.builder()
                .name(file.getName())
                .storageId(file.getId())
                .storageRoute(file.getStorageRoute())
                .processId(process.getId())
                .analysisId(analysisId)
                .status(OcrStatus.PENDING)
                .build();
    }

}