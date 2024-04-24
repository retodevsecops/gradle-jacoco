package com.consubanco.usecase.file;

import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
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

import static com.consubanco.model.entities.document.constant.DocumentNames.OFFICIAL_ID;
import static com.consubanco.model.entities.document.constant.DocumentNames.PARTS_OFFICIAL_ID;
import static com.consubanco.model.entities.file.constant.FileConstants.attachmentsDirectory;
import static com.consubanco.model.entities.file.util.AttachmentValidatorUtil.checkAttachments;
import static com.consubanco.model.entities.file.util.AttachmentValidatorUtil.checkAttachmentsSize;

@RequiredArgsConstructor
public class UploadAgreementFilesUseCase {

    private final AgreementGateway agreementGateway;
    private final FileRepository fileRepository;
    private final PDFDocumentGateway pdfDocument;
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final BuildAgreementDocumentsUseCase buildAgreementDocumentsUseCase;
    private final BuildCompoundDocumentsUseCase buildCompoundDocumentsUseCase;

    public Mono<Map<String, String>> execute(String processId, List<FileUploadVO> attachments) {
        return checkAttachmentsSize(attachments, fileRepository.getMaxSizeOfFileInMBAllowed())
                .then(getProcessByIdUseCase.execute(processId))
                .flatMap(process -> startProcess(attachments, process));
    }

    private Mono<Map<String, String>> startProcess(List<FileUploadVO> attachments, Process process) {
        return agreementGateway.findByNumber(process.getAgreementNumber())
                .flatMap(agreement -> checkAttachments(agreement.getAttachments(), attachments)
                        .flatMap(validAttachments -> uploadAllDocuments(process, validAttachments, agreement)));
    }

    private Mono<Map<String, String>> uploadAllDocuments(Process process, List<FileUploadVO> attachments, Agreement agreement) {
        Flux<File> uploadAttachments = uploadAttachments(attachments, process.getOffer().getId());
        Flux<File> uploadGeneratedDocuments = buildAgreementDocumentsUseCase.execute(process, agreement.getDocuments());
        Flux.merge(uploadAttachments, uploadGeneratedDocuments)
                .collectList()
                .flatMap(files -> buildCompoundDocumentsUseCase.execute(process, files))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
        return Mono.just(Map.of("message", "The validations were successful, the file uploading process starts..."));
    }

    private Flux<File> uploadAttachments(List<FileUploadVO> attachments, String offerId) {
        return buildAttachmentList(attachments, offerId)
                .concatWith(generateOfficialIdPdf(attachments, offerId))
                .parallel()
                .runOn(Schedulers.parallel())
                .concatMap(fileRepository::save)
                .sequential();
    }

    private Flux<File> buildAttachmentList(List<FileUploadVO> attachments, String offerId) {
        return Flux.fromIterable(attachments)
                .filter(attachment -> !PARTS_OFFICIAL_ID.contains(attachment.getName()))
                .map(fileUploadVO -> File.builder()
                        .name(fileUploadVO.getName())
                        .content(fileUploadVO.getContent())
                        .directoryPath(attachmentsDirectory(offerId))
                        .extension(fileUploadVO.getExtension())
                        .build());
    }

    private Mono<File> generateOfficialIdPdf(List<FileUploadVO> attachments, String offerId) {
        return Flux.fromIterable(attachments)
                .filter(attachment -> PARTS_OFFICIAL_ID.contains(attachment.getName()))
                .map(FileUploadVO::getContent)
                .collectList()
                .flatMap(pdfDocument::generatePdfWithImages)
                .map(officialID -> File.builder()
                        .name(OFFICIAL_ID)
                        .content(officialID)
                        .directoryPath(attachmentsDirectory(offerId))
                        .extension(FileExtensions.PDF)
                        .build());
    }

}
