package com.consubanco.usecase.file;

import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
import com.consubanco.model.entities.document.gateway.PdfDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileConvertGateway;
import com.consubanco.model.entities.file.gateway.FileGateway;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.util.FileUtil;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.document.BuildPayloadUseCase;
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
import static com.consubanco.model.entities.file.constant.FileConstants.documentsDirectory;
import static com.consubanco.model.entities.file.util.AttachmentValidatorUtil.checkAttachments;
import static com.consubanco.model.entities.file.util.AttachmentValidatorUtil.checkAttachmentsSize;

@RequiredArgsConstructor
public class UploadAgreementFilesUseCase {

    private final BuildPayloadUseCase buildPayloadUseCase;
    private final AgreementGateway agreementGateway;
    private final FileRepository fileRepository;
    private final FileGateway fileGateway;
    private final FileConvertGateway fileConvertGateway;
    private final PdfDocumentGateway pdfDocument;
    private final GetProcessByIdUseCase getProcessByIdUseCase;

    public Mono<Map<String, String>> execute(String processId, List<FileUploadVO> attachments) {
        return checkAttachmentsSize(attachments, fileRepository.getMaxSizeOfFileInMBAllowed())
                .then(getProcessByIdUseCase.execute(processId))
                .flatMap(process -> agreementGateway.findByNumber(process.getAgreementNumber())
                        .flatMap(agreement -> checkAttachments(agreement.getAttachments(), attachments).thenReturn(agreement))
                        .flatMap(agreement -> uploadAllDocuments(process, attachments, agreement)));
    }

    private Mono<Map<String, String>> uploadAllDocuments(Process process, List<FileUploadVO> attachments, Agreement agreement) {
        Flux<File> uploadAttachments = uploadAttachments(attachments, process.getOffer().getId());
        Flux<File> uploadGeneratedDocuments = uploadGeneratedDocuments(process, agreement.getDocuments());
        Flux.merge(uploadAttachments, uploadGeneratedDocuments)
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

    private Flux<File> uploadGeneratedDocuments(Process process, List<Agreement.Document> documents) {
        return buildPayloadUseCase.execute(process.getId())
                .flatMapMany(payload -> generatedDocuments(documents, payload, process.getOffer().getId()))
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(fileRepository::save)
                .sequential();
    }

    private Flux<File> generatedDocuments(List<Agreement.Document> documents, Map<String, Object> payload, String offerId) {
        return Flux.fromIterable(documents)
                .flatMap(document -> Flux.fromIterable(document.getFields()))
                .map(Agreement.Document.Field::getTechnicalName)
                .distinct()
                .parallel()
                .runOn(Schedulers.parallel())
                .map("csb/"::concat)
                .flatMap(documentPath -> generateDocument(documentPath, payload, offerId))
                .sequential();
    }

    private Mono<File> generateDocument(String documentPath, Map<String, Object> payload, String offerId) {
        return fileGateway.generate(documentPath, payload)
                .flatMap(fileConvertGateway::encodedFile)
                .map(documentContent -> File.builder()
                        .name(FileUtil.getFileNameFromPath(documentPath))
                        .content(documentContent)
                        .directoryPath(documentsDirectory(offerId))
                        .extension(FileExtensions.PDF)
                        .build());
    }

}
