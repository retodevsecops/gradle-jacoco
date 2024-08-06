package com.consubanco.usecase.document.usecase;

import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.document.gateway.PDFDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.regex.Pattern;

import static com.consubanco.model.entities.document.constant.DocumentNames.UNSIGNED_APPLICANT_RECORD;
import static com.consubanco.model.entities.document.message.DocumentBusinessMessage.CNCA_NOT_FOUND;
import static com.consubanco.model.entities.document.message.DocumentBusinessMessage.DOCUMENT_NOT_FOUND;
import static com.consubanco.model.entities.document.message.DocumentMessage.GENERATE_CNCA;
import static com.consubanco.model.entities.document.message.DocumentMessage.documentNotFound;

@RequiredArgsConstructor
public class BuildCompoundDocumentsUseCase {

    private static final String PATTERN_NAME_ATTACHMENT = ".*-\\d+$";
    private final CustomLogger logger;
    private final AgreementConfigRepository agreementConfigRepository;
    private final PDFDocumentGateway pdfDocumentGateway;
    private final FileRepository fileRepository;
    private final GenerateNom151UseCase generateNom151UseCase;

    public Mono<Void> execute(Process process, List<File> docsGenerated) {
        Mono<List<File>> attachments = getAttachmentsByOfferId(process.getOfferId());
        Mono<File> cncaLetter = getCNCALetterByOfferId(process.getOfferId());
        return Mono.zip(attachments, cncaLetter)
                .map(tuple -> mergeFiles(docsGenerated, tuple.getT1(), tuple.getT2()))
                .flatMap(allFiles -> processCompoundDocuments(process, allFiles))
                .then();
    }

    private Mono<List<File>> getAttachmentsByOfferId(String offerId) {
        Pattern pattern = Pattern.compile(PATTERN_NAME_ATTACHMENT);
        return Mono.just(offerId)
                .map(FileConstants::attachmentsDirectory)
                .flatMapMany(fileRepository::listByFolderWithoutUrls)
                .filter(file -> !pattern.matcher(file.getName()).matches())
                .collectList();
    }

    private Mono<File> getCNCALetterByOfferId(String offerId) {
        return Mono.just(offerId)
                .map(FileConstants::cncaLetterRoute)
                .flatMap(fileRepository::getByNameWithoutSignedUrl)
                .switchIfEmpty(ExceptionFactory.monoBusiness(CNCA_NOT_FOUND, GENERATE_CNCA));
    }

    private Mono<Tuple2<File, List<File>>> processCompoundDocuments(Process process, List<File> allFiles) {
        String directory = FileConstants.documentsDirectory(process.getOffer().getId());
        String agreementNumber = process.getAgreementNumber();
        Mono<File> unsignedApplicantRecord = createUnsignedApplicantRecord(allFiles, directory);
        Flux<File> compoundDocuments = createConfiguredCompoundDocuments(agreementNumber, allFiles, directory);
        return Mono.zip(unsignedApplicantRecord, compoundDocuments.collectList())
                .doOnSuccess(e -> generateSignedApplicantRecord(process));
    }

    private Flux<File> createConfiguredCompoundDocuments(String agreementNumber, List<File> files, String directory) {
        return agreementConfigRepository.getConfigByAgreement(agreementNumber)
                .filter(AgreementConfigVO::checkCompoundDocuments)
                .map(AgreementConfigVO::getCompoundDocuments)
                .flatMapMany(Flux::fromIterable)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(document -> processCompoundDocument(document, files, directory))
                .sequential();
    }

    private Mono<File> processCompoundDocument(AgreementConfigVO.CompoundDocument compoundDocument,
                                               List<File> files,
                                               String directory) {
        return getContentCompoundDocument(compoundDocument, files)
                .map(content -> buildCompundDocumentFile(compoundDocument.getName(), content, directory))
                .flatMap(fileRepository::save);
    }

    private Mono<String> getContentCompoundDocument(AgreementConfigVO.CompoundDocument compoundDocument,
                                                    List<File> files) {
        return Flux.fromIterable(compoundDocument.getDocuments())
                .flatMap(documentData -> getFileContentByName(documentData, files))
                .collectList()
                .flatMap(pdfDocumentGateway::merge);
    }

    private Mono<String> getFileContentByName(AgreementConfigVO.DocumentData documentData, List<File> files) {
        return findFileByName(documentData, files)
                .switchIfEmpty(ExceptionFactory.monoBusiness(DOCUMENT_NOT_FOUND, documentNotFound(documentData.getName())))
                .flatMap(file -> extractPageIfNeeded(documentData.getPage(), file));
    }

    private Mono<File> findFileByName(AgreementConfigVO.DocumentData documentData, List<File> files) {
        return Flux.fromIterable(files)
                .filter(file -> documentData.getName().equalsIgnoreCase(file.getName()))
                .next();
    }

    private Mono<String> extractPageIfNeeded(Integer pageNumber, File file) {
        return Mono.justOrEmpty(pageNumber)
                .flatMap(page -> pdfDocumentGateway.getPageFromPDF(file.getContent(), page))
                .defaultIfEmpty(file.getContent());
    }

    private Mono<File> createUnsignedApplicantRecord(List<File> files, String directory) {
        List<String> base64Documents = files.stream().map(File::getContent).toList();
        return pdfDocumentGateway.mergeAndAddBlankPage(base64Documents)
                .map(documentContent -> buildCompundDocumentFile(UNSIGNED_APPLICANT_RECORD, documentContent, directory))
                .flatMap(fileRepository::save);
    }

    private void generateSignedApplicantRecord(Process process) {
        generateNom151UseCase.execute(process)
                .doFinally(e -> logger.info("The file signed with nom151 was generated from the creation of compound documents for process.", process))
                .subscribeOn(Schedulers.parallel())
                .subscribe();
    }

    private File buildCompundDocumentFile(String name, String content, String directory) {
        return File.builder()
                .name(name)
                .content(content)
                .directoryPath(directory)
                .extension(FileExtensions.PDF)
                .build();
    }

    private List<File> mergeFiles(List<File> files, List<File> attachments, File cncaLetter) {
        files.add(cncaLetter);
        files.addAll(attachments.stream()
                .filter(attachment -> !checkIfExists(files, attachment.getName()))
                .toList());
        return files;
    }

    private Boolean checkIfExists(List<File> files, String nameAttachment) {
        return files.stream().anyMatch(file -> file.getName().equalsIgnoreCase(nameAttachment));
    }

}