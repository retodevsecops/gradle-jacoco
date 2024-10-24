package com.consubanco.usecase.document.usecase;

import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.document.constant.DocumentNames;
import com.consubanco.model.entities.document.gateway.PDFDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.util.FileFactoryUtil;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.agreement.GetAgreementConfigUseCase;
import com.consubanco.usecase.file.helpers.FileHelper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static com.consubanco.model.entities.document.constant.DocumentNames.UNSIGNED_APPLICANT_RECORD;
import static com.consubanco.model.entities.document.message.DocumentBusinessMessage.CNCA_NOT_FOUND;
import static com.consubanco.model.entities.document.message.DocumentBusinessMessage.DOCUMENT_NOT_FOUND;
import static com.consubanco.model.entities.document.message.DocumentMessage.GENERATE_CNCA;
import static com.consubanco.model.entities.document.message.DocumentMessage.documentNotFound;

@RequiredArgsConstructor
public class BuildCompoundDocumentsUseCase {

    private final CustomLogger logger;
    private final GetAgreementConfigUseCase getAgreementConfigUseCase;
    private final PDFDocumentGateway pdfDocumentGateway;
    private final FileHelper fileHelper;
    private final GenerateNom151UseCase generateNom151UseCase;

    public Mono<Void> execute(Process process, Agreement agreement) {
        Mono<AgreementConfigVO> agreementConfig = getAgreementConfigUseCase.execute(process.getAgreementNumber());
        Mono<List<File>> offerFiles = fileHelper.filesByOfferWithoutUrls(process.getOfferId());
        return Mono.zip(offerFiles, agreementConfig)
                .flatMap(tuple -> processCompoundDocuments(tuple.getT1(), tuple.getT2(), agreement, process))
                .then();
    }

    private Mono<File> processCompoundDocuments(List<File> files,
                                                AgreementConfigVO agreementConfig,
                                                Agreement agreement,
                                                Process process) {
        String directory = FileConstants.documentsDirectory(process.getOffer().getId());
        return createConfiguredCompoundDocuments(agreementConfig, files, directory)
                .flatMap(docs -> createUnsignedApplicantRecord(docs, directory, agreement, agreementConfig))
                .doOnSuccess(e -> generateSignedApplicantRecord(process));
    }

    private Mono<List<File>> createConfiguredCompoundDocuments(AgreementConfigVO agreementConfigVO,
                                                               List<File> initialFiles,
                                                               String directory) {
        AtomicReference<List<File>> filesRef = new AtomicReference<>(new ArrayList<>(initialFiles));
        return Flux.fromIterable(agreementConfigVO.getCompoundDocuments())
                .flatMap(compoundDocument -> processCompoundDocument(compoundDocument, filesRef.get(), directory)
                        .map(file -> {
                            List<File> updatedFiles = new ArrayList<>(filesRef.get());
                            updatedFiles.add(file);
                            filesRef.set(updatedFiles);
                            return updatedFiles;
                        })
                )
                .last(filesRef.get());
    }

    private Mono<File> processCompoundDocument(AgreementConfigVO.CompoundDocument compoundDocument,
                                               List<File> files,
                                               String directory) {
        return getContentCompoundDocument(compoundDocument, files)
                .map(document -> FileFactoryUtil.buildPDF(compoundDocument.getName(), document, directory))
                .flatMap(fileHelper::save);
    }

    private Mono<String> getContentCompoundDocument(AgreementConfigVO.CompoundDocument compoundDocument,
                                                    List<File> files) {
        return Flux.fromIterable(compoundDocument.getDocuments())
                .flatMap(documentData -> getFileContentByName(documentData, files, compoundDocument.getName()))
                .collectList()
                .flatMap(pdfDocumentGateway::merge);
    }

    private Mono<String> getFileContentByName(AgreementConfigVO.DocumentData documentData, List<File> files, String compoundDocumentName) {
        String documentDataName = documentData.getName();
        return findFileByName(documentDataName, files)
                .switchIfEmpty(ExceptionFactory.monoBusiness(DOCUMENT_NOT_FOUND, documentNotFound(documentDataName, compoundDocumentName)))
                .flatMap(file -> extractPageIfNeeded(documentData.getPage(), file));
    }

    private Mono<File> findFileByName(String documentDataName, List<File> files) {
        return Flux.fromIterable(files)
                .filter(file -> documentDataName.equalsIgnoreCase(file.getName()))
                .next();
    }

    private Mono<String> extractPageIfNeeded(Integer pageNumber, File file) {
        return Mono.justOrEmpty(pageNumber)
                .flatMap(page -> pdfDocumentGateway.getPageFromPDF(file.getContent(), page))
                .defaultIfEmpty(file.getContent());
    }

    private Mono<File> createUnsignedApplicantRecord(List<File> files,
                                                     String directory,
                                                     Agreement agreement,
                                                     AgreementConfigVO agreementConfig) {
        List<String> base64Documents = this.filesForApplicantRecord(files, agreement, agreementConfig);
        return pdfDocumentGateway.mergeAndAddBlankPage(base64Documents)
                .map(document -> FileFactoryUtil.buildPDF(UNSIGNED_APPLICANT_RECORD, document, directory))
                .flatMap(fileHelper::save);
    }

    private List<String> filesForApplicantRecord(List<File> files, Agreement agreement, AgreementConfigVO agreementConfig) {
        String cncaLetterContent = getCncaLetterContent(files);
        List<String> agreementDocuments = new ArrayList<>(agreement.getSortedDocuments());
        agreementDocuments.addAll(agreementConfig.getAttachmentsTechnicalNames());
        Stream<String> documentContents = agreementDocuments.stream()
                .map(documentName -> retrieveDocumentContent(files, documentName))
                .filter(Objects::nonNull);
        return Stream.concat(documentContents, Stream.of(cncaLetterContent))
                .toList();
    }

    private String getCncaLetterContent(List<File> files) {
        String cncaContent = retrieveDocumentContent(files, DocumentNames.CNCA_LETTER);
        if (Objects.nonNull(cncaContent)) return cncaContent;
        throw ExceptionFactory.buildBusiness(GENERATE_CNCA, CNCA_NOT_FOUND);
    }

    private String retrieveDocumentContent(List<File> files, String documentName) {
        return files.stream()
                .filter(file -> file.getName().equalsIgnoreCase(documentName))
                .map(File::getContent)
                .findFirst()
                .orElse(null);
    }

    private void generateSignedApplicantRecord(Process process) {
        generateNom151UseCase.execute(process)
                .doFinally(e -> logger.info("The file signed with nom151 was generated from the creation of compound documents for process.", process))
                .subscribeOn(Schedulers.parallel())
                .subscribe();
    }

}