package com.consubanco.usecase.file;

import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.document.gateway.PDFDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.document.BuildAgreementDocumentsUseCase;
import com.consubanco.usecase.document.BuildCompoundDocumentsUseCase;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

import static com.consubanco.model.entities.file.constant.FileConstants.attachmentsDirectory;
import static com.consubanco.model.entities.file.util.AttachmentValidatorUtil.checkAttachments;
import static com.consubanco.model.entities.file.util.AttachmentValidatorUtil.checkAttachmentsSize;

@RequiredArgsConstructor
public class UploadAgreementFilesUseCase {

    private final AgreementGateway agreementGateway;
    private final AgreementConfigRepository agreementConfigRepository;
    private final FileRepository fileRepository;
    private final PDFDocumentGateway pdfDocumentGateway;
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final BuildAgreementDocumentsUseCase buildAgreementDocumentsUseCase;
    private final BuildCompoundDocumentsUseCase buildCompoundDocumentsUseCase;

    public Mono<Map<String, String>> execute(String processId, List<FileUploadVO> attachments) {
        return checkAttachmentsSize(attachments, fileRepository.getMaxSizeOfFileInMBAllowed())
                .then(getProcessByIdUseCase.execute(processId))
                .flatMap(process -> startProcess(attachments, process));
    }

    private Mono<Map<String, String>> startProcess(List<FileUploadVO> attachments, Process process) {
        Mono<Agreement> agreement = agreementGateway.findByNumber(process.getAgreementNumber());
        Mono<AgreementConfigVO> agreementConfig = agreementConfigRepository.getConfigByAgreement(process.getAgreementNumber());
        return Mono.zip(agreement, agreementConfig)
                .flatMap(tuple -> checkAttachments(tuple.getT2().getAttachmentsDocuments(), attachments)
                        .flatMap(validAttachments -> uploadAllDocuments(process, validAttachments, tuple.getT1())));
    }

    private Mono<Map<String, String>> uploadAllDocuments(Process process, List<FileUploadVO> attachments, Agreement agreement) {
        Flux<File> uploadAttachments = uploadAttachments(attachments, process.getOffer().getId());
        Flux<File> uploadGeneratedDocuments = buildAgreementDocumentsUseCase.execute(process, agreement.getDocuments());
        return Flux.merge(uploadGeneratedDocuments, uploadAttachments)
                .collectList()
                .flatMap(files -> buildCompoundDocumentsUseCase.execute(process, files))
                .thenReturn(Map.of("message", "Files uploaded and generated successfully."));
    }

    private Flux<File> uploadAttachments(List<FileUploadVO> attachments, String offerId) {
        return buildAttachmentList(attachments, offerId)
                .parallel()
                .runOn(Schedulers.parallel())
                .concatMap(fileRepository::save)
                .sequential();
    }

    private Flux<File> buildAttachmentList(List<FileUploadVO> attachments, String offerId) {
        return Flux.fromIterable(attachments)
                .flatMap(fileUploadVO -> convertAttachmentToPDF(fileUploadVO)
                        .map(pdf -> File.builder()
                                .name(fileUploadVO.getName())
                                .content(pdf)
                                .directoryPath(attachmentsDirectory(offerId))
                                .extension(FileExtensions.PDF)
                                .build()));
    }

    private Mono<String> convertAttachmentToPDF(FileUploadVO fileUploadVO) {
        return Mono.just(fileUploadVO)
                .filter(FileUploadVO::isNotPDF)
                .map(FileUploadVO::getContent)
                .map(List::of)
                .flatMap(pdfDocumentGateway::generatePdfWithImages)
                .defaultIfEmpty(fileUploadVO.getContent());
    }

}