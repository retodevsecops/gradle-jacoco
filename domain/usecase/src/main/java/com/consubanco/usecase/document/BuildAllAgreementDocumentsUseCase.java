package com.consubanco.usecase.document;

import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.agreement.AgreementUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.List;

@RequiredArgsConstructor
public class BuildAllAgreementDocumentsUseCase {

    private final AgreementUseCase agreementUseCase;
    private final BuildAgreementDocumentsUseCase buildAgreementDocumentsUseCase;
    private final BuildCompoundDocumentsUseCase buildCompoundDocumentsUseCase;
    private final FileRepository fileRepository;

    public Mono<Void> execute(Process process) {
        Mono<Agreement> agreement = agreementUseCase.findByNumber(process.getAgreementNumber());
        Mono<List<File>> attachments = getAttachmentsByOfferId(process.getOfferId());
        return Mono.zip(agreement, attachments, Mono.just(process))
                .flatMap(TupleUtils.function(this::generateAllDocuments));
    }

    private Mono<Void> generateAllDocuments(Agreement agreement, List<File> attachments, Process process) {
        return buildAgreementDocumentsUseCase.execute(process, agreement.getDocuments())
                .collectList()
                .map(docs -> joinFileLists(docs, attachments))
                .flatMap(docs -> buildCompoundDocumentsUseCase.execute(process, docs));
    }

    private List<File> joinFileLists(List<File> docs, List<File> attachments) {
        docs.addAll(attachments);
        return docs;
    }

    private Mono<List<File>> getAttachmentsByOfferId(String offerId) {
        return Mono.just(offerId)
                .map(FileConstants::attachmentsDirectory)
                .flatMapMany(fileRepository::listByFolderWithoutUrls)
                .collectList();
    }

}
