package com.consubanco.usecase.document;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.document.gateway.PDFDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.consubanco.model.entities.document.message.DocumentBusinessMessage.DOCUMENT_NOT_FOUND;
import static com.consubanco.model.entities.document.message.DocumentMessage.documentNotFound;
import static com.consubanco.model.entities.file.constant.FileConstants.documentsDirectory;

@RequiredArgsConstructor
public class BuildCompoundDocumentsUseCase {

    private final AgreementConfigRepository agreementConfigRepository;
    private final PDFDocumentGateway pdfDocumentGateway;
    private final FileRepository fileRepository;

    public Mono<Void> execute(Process process, List<File> files) {
        return agreementConfigRepository.getConfigByAgreement(process.getAgreementNumber())
                .filter(AgreementConfigVO::checkCompoundDocuments)
                .map(AgreementConfigVO::getCompoundDocuments)
                .flatMapMany(Flux::fromIterable)
                .flatMap(document -> processCompoundDocument(process.getOffer().getId(), files, document))
                .then();
    }

    private Mono<File> processCompoundDocument(String offerId,
                                               List<File> files,
                                               AgreementConfigVO.CompoundDocument compoundDocument) {
        return getContentCompoundDocument(compoundDocument, files)
                .map(content -> buildCompundDocumentFile(offerId, compoundDocument.getName(), content))
                .doOnNext(e -> System.out.println(e.getContent()))
                .flatMap(fileRepository::save);
    }

    private Mono<String> getContentCompoundDocument(AgreementConfigVO.CompoundDocument compoundDocument,
                                                    List<File> files) {
        // TODO: SE DEBE TRABAJAR EN EL MERGE DE DOCUMENTOS IDENTIFICAR CUANDO ES PDF Y CUANDO IMAGEN
        return Flux.fromIterable(compoundDocument.getDocuments())
                .flatMap(documentData -> getFileContentByName(documentData, files))
                .collectList()
                .flatMap(pdfDocumentGateway::merge);
    }

    private File buildCompundDocumentFile(String offerId, String name, String content) {
        return File.builder()
                .name(name)
                .content(content)
                .directoryPath(documentsDirectory(offerId))
                .extension(FileExtensions.PDF)
                .build();
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

}
