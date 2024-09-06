package com.consubanco.usecase.ocr.helpers;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.util.FilterListUtil;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.file.helpers.GetAttachmentsByOfferHelper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.List;

import static com.consubanco.model.entities.ocr.message.OcrBusinessMessage.OCR_NOT_FOUND;
import static com.consubanco.model.entities.ocr.message.OcrMessage.ocrNotAssociated;
@RequiredArgsConstructor
public class GetOcrAttachmentsHelper {

    private final GetAttachmentsByOfferHelper getAttachmentsByOfferHelper;
    private final OcrDocumentRepository ocrDocumentRepository;

    public Mono<List<OcrDocument>> execute(Process process, AgreementConfigVO agreementConfigVO) {
        Flux<File> attachments = getAttachmentsByOfferHelper.execute(process.getOfferId());
        Flux<OcrDocument> ocrDocuments = ocrDocumentRepository.findByProcessId(process.getId());
        return Mono.zip(attachments.collectList(), ocrDocuments.collectList(), Mono.just(agreementConfigVO))
                .map(TupleUtils.function(this::filter));
    }

    private List<OcrDocument> filter(List<File> attachments, List<OcrDocument> ocrDocuments, AgreementConfigVO agreementConfig) {
        List<File> ocrAttachments = getOcrAttachments(attachments, agreementConfig);
        return ocrDocumentsByAttachments(ocrDocuments, ocrAttachments);
    }

    private List<File> getOcrAttachments(List<File> attachments, AgreementConfigVO agreementConfig) {
        List<String> ocrDocumentsConfig = getAttachmentsConfiguredWithOcr(agreementConfig);
        return FilterListUtil.removeCompoundAttachments(attachments)
                .parallelStream()
                .filter(attachment -> ocrDocumentsConfig.contains(attachment.baseFileName()))
                .toList();
    }

    private List<String> getAttachmentsConfiguredWithOcr(AgreementConfigVO agreementConfig) {
        return agreementConfig.getAttachmentsDocuments()
                .parallelStream()
                .filter(AttachmentConfigVO::getIsOcr)
                .map(AttachmentConfigVO::getTechnicalName)
                .toList();
    }

    private List<OcrDocument> ocrDocumentsByAttachments(List<OcrDocument> ocrDocuments, List<File> ocrAttachments) {
        return ocrAttachments.parallelStream()
                .map(file -> getOcrDocumentByStorageId(ocrDocuments, file))
                .map(OcrDocument::checkSuccessStatus)
                .toList();

    }

    private OcrDocument getOcrDocumentByStorageId(List<OcrDocument> ocrDocuments, File attachment) {
        return ocrDocuments.parallelStream()
                .filter(ocrDocument -> ocrDocument.getStorageId().equals(attachment.getId()))
                .findFirst()
                .orElseThrow(() -> ExceptionFactory.buildBusiness(ocrNotAssociated(attachment), OCR_NOT_FOUND));
    }

}