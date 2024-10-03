package com.consubanco.usecase.ocr.usecase;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.util.FilterListUtil;
import com.consubanco.model.entities.file.util.MetadataUtil;
import com.consubanco.model.entities.file.vo.AttachmentStatus;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.constant.OcrStatus;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentRepository;
import com.consubanco.usecase.agreement.GetAgreementConfigUseCase;
import com.consubanco.usecase.file.helpers.GetAttachmentsByOfferHelper;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.List;

import static com.consubanco.model.entities.ocr.message.OcrBusinessMessage.OCR_NOT_FOUND;
import static com.consubanco.model.entities.ocr.message.OcrMessage.ocrNotAssociated;

@RequiredArgsConstructor
public class GetStatusAttachmentUseCase {

    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final GetAttachmentsByOfferHelper getAttachmentsByOfferHelper;
    private final GetAgreementConfigUseCase getAgreementConfigUseCase;
    private final OcrDocumentRepository ocrDocumentRepository;

    public Mono<AttachmentStatus> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(process -> {
                    Mono<List<File>> attachments = this.getNonRetrievedFilesByOfferId(process.getOfferId());
                    Flux<OcrDocument> ocrDocuments = ocrDocumentRepository.findByProcessId(processId);
                    Mono<AgreementConfigVO> agreementConfig = getAgreementConfigUseCase.execute(process.getAgreementNumber());
                    return Mono.zip(attachments, ocrDocuments.collectList(), agreementConfig)
                            .map(TupleUtils.function(this::verify));
                });
    }

    private Mono<List<File>> getNonRetrievedFilesByOfferId(String offerId) {
        return getAttachmentsByOfferHelper.execute(offerId)
                .filter(file -> !MetadataUtil.isRetrievedFile(file.getMetadata()))
                .collectList();
    }

    private AttachmentStatus verify(List<File> attachments, List<OcrDocument> ocrDocuments, AgreementConfigVO agreementConfig) {
        List<File> ocrAttachments = getOcrAttachments(attachments, agreementConfig);
        List<OcrDocument> invalidOcrDocuments = getInvalidOcrDocuments(ocrDocuments, ocrAttachments);
        return new AttachmentStatus(invalidOcrDocuments);
    }

    private List<File> getOcrAttachments(List<File> attachments, AgreementConfigVO agreementConfig) {
        List<String> ocrDocumentsConfig = getAttachmentsConfiguredWithOcr(agreementConfig);
        return FilterListUtil.removeCompoundAttachments(attachments)
                .stream()
                .filter(attachment -> ocrDocumentsConfig.contains(attachment.baseFileName()))
                .toList();
    }

    private List<OcrDocument> getInvalidOcrDocuments(List<OcrDocument> ocrDocuments, List<File> ocrAttachments) {
        return ocrAttachments.stream()
                .map(attachment -> getOcrDocumentByStorageId(ocrDocuments, attachment))
                .filter(ocrDocument -> !ocrDocument.getStatus().equals(OcrStatus.SUCCESS))
                .toList();
    }

    private OcrDocument getOcrDocumentByStorageId(List<OcrDocument> ocrDocuments, File attachment) {
        return ocrDocuments.stream()
                .filter(ocrDocument -> ocrDocument.getStorageId().equals(attachment.getId()))
                .findFirst()
                .orElseThrow(() -> ExceptionFactory.buildBusiness(ocrNotAssociated(attachment), OCR_NOT_FOUND));
    }

    private List<String> getAttachmentsConfiguredWithOcr(AgreementConfigVO agreementConfig) {
        return agreementConfig.getAttachmentsDocuments()
                .stream()
                .filter(AttachmentConfigVO::getIsOcr)
                .map(AttachmentConfigVO::getTechnicalName)
                .toList();
    }

}