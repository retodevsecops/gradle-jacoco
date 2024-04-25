package com.consubanco.usecase.document;

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

import java.util.List;

import static com.consubanco.model.entities.document.constant.DocumentNames.APPLICANT_RECORD;
import static com.consubanco.model.entities.document.message.DocumentBusinessMessage.DOCUMENT_NOT_FOUND;
import static com.consubanco.model.entities.document.message.DocumentMessage.documentNotFound;

@RequiredArgsConstructor
public class BuildCompoundDocumentsUseCase {

    private final AgreementConfigRepository agreementConfigRepository;
    private final PDFDocumentGateway pdfDocumentGateway;
    private final FileRepository fileRepository;

    public Mono<Void> execute(Process process, List<File> files) {
        String directory = FileConstants.documentsDirectory(process.getOffer().getId());
        String agreementNumber = process.getAgreementNumber();
        return createApplicantRecord(files, directory)
                .flatMapMany(e -> createConfiguredCompoundDocuments(agreementNumber, files, directory))
                .then();
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
                .map(content -> buildCompundDocumentFile(directory, compoundDocument.getName(), content))
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

    private Mono<File> createApplicantRecord(List<File> files, String directory) {
        List<String> base64Documents = files.stream().map(File::getContent).toList();
        return pdfDocumentGateway.merge(base64Documents)
                .map(documentContent -> buildCompundDocumentFile(APPLICANT_RECORD, documentContent, directory))
                .flatMap(fileRepository::save);
    }

    private File buildCompundDocumentFile(String name, String content, String directory) {
        return File.builder()
                .name(name)
                .content(content)
                .directoryPath(directory)
                .extension(FileExtensions.PDF)
                .build();
    }

}
