package com.consubanco.usecase.file;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateways.AgreementGateway;
import com.consubanco.model.entities.document.gateway.PdfDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateways.FileConvertGateway;
import com.consubanco.model.entities.file.gateways.FileGateway;
import com.consubanco.model.entities.file.gateways.FileRepository;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.usecase.document.BuildPayloadUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.consubanco.model.entities.document.constant.DocumentNames.OFFICIAL_ID;
import static com.consubanco.model.entities.document.constant.DocumentNames.PARTS_OFFICIAL_ID;
import static com.consubanco.model.entities.file.constant.FileConstants.attachmentsDirectory;
import static com.consubanco.model.entities.file.constant.FileConstants.documentsDirectory;
import static com.consubanco.model.entities.file.message.FileBusinessMessage.MISSING_ATTACHMENT;

@RequiredArgsConstructor
public class UploadAgreementFilesUseCase {

    private final BuildPayloadUseCase buildPayloadUseCase;
    private final AgreementGateway agreementGateway;
    private final FileRepository fileRepository;
    private final FileGateway fileGateway;
    private final FileConvertGateway fileConvertGateway;
    private final PdfDocumentGateway pdfDocument;

    public Mono<Void> execute(String agreementNumber, String offerId, List<FileUploadVO> attachments) {
        return agreementGateway.findByNumber(agreementNumber)
                .flatMap(agreement -> checkAttachments(agreement.getAttachments(), attachments).thenReturn(agreement))
                .flatMap(agreement -> uploadAllDocuments(offerId, attachments, agreement));
    }

    private Mono<Void> checkAttachments(List<Agreement.Document> requiredAttachments, List<FileUploadVO> attachmentsProvided) {
        List<String> providedNames = nameAttachments(attachmentsProvided);
        return Flux.fromIterable(requiredAttachments)
                .filter(Agreement.Document::getIsRequired)
                .map(Agreement.Document::getTechnicalName)
                .filter(required -> !providedNames.contains(required))
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    throw ExceptionFactory.buildBusiness((String.join(", ", list)), MISSING_ATTACHMENT);
                })
                .then();
    }

    private List<String> nameAttachments(List<FileUploadVO> attachments) {
        return attachments.stream()
                .map(FileUploadVO::getName)
                .collect(Collectors.toList());
    }

    private Mono<Void> uploadAllDocuments(String offerId, List<FileUploadVO> attachments, Agreement agreement) {
        Flux<File> uploadAttachments = uploadAttachments(attachments, offerId);
        Flux<File> uploadGeneratedDocuments = uploadGeneratedDocuments(agreement.getDocuments(), offerId);
        Flux.merge(uploadAttachments, uploadGeneratedDocuments)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
        return Mono.empty();
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
                        .build());
    }

    private Flux<File> uploadGeneratedDocuments(List<Agreement.Document> documents, String offerId) {
        // TODO: DEBO TRABAJAR EN LA GENERACION DEL PEYLOAD Y DISMINUIR EL TIEMPO DE DE RESPUESTA
        return buildPayloadUseCase.execute()
                .flatMapMany(payload -> generatedDocuments(documents, payload, offerId))
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
                        .name(getDocumentName(documentPath))
                        .content(documentContent)
                        .directoryPath(documentsDirectory(offerId))
                        .build());
    }

    private String getDocumentName(String documentPath) {
        String[] parts = documentPath.split("/");
        return parts[parts.length - 1];
    }

}
