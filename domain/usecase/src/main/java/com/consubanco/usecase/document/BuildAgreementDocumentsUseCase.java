package com.consubanco.usecase.document;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.document.gateway.DocumentGateway;
import com.consubanco.model.entities.document.util.DocumentUtil;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileConvertGateway;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

import static com.consubanco.model.entities.document.message.DocumentBusinessMessage.NOT_GENERATED;

@RequiredArgsConstructor
public class BuildAgreementDocumentsUseCase {

    private final BuildPayloadDocumentUseCase buildPayloadUseCase;
    private final FileRepository fileRepository;
    private final DocumentGateway documentGateway;
    private final FileConvertGateway fileConvertGateway;

    public Flux<File> execute(Process process, List<Agreement.Document> documents) {
        List<String> documentsToGenerate = getListDocumentsToGenerate(documents);
        String directory = FileConstants.documentsDirectory(process.getOffer().getId());
        return buildPayloadUseCase.execute(process.getId())
                .flatMap(payload -> documentGateway.generateMultiple(documentsToGenerate, payload))
                .flatMapMany(documentUrlsMap -> generateFilesFromUrls(documentUrlsMap, documentsToGenerate, directory))
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(fileRepository::save)
                .sequential();
    }

    private Flux<File> generateFilesFromUrls(Map<String, String> documentUrls,
                                             List<String> documentsToGenerate,
                                             String directory) {
        return Flux.fromIterable(documentsToGenerate)
                .map(DocumentUtil::getDocumentNameFromPath)
                .flatMap(documentName -> getDocumentContent(documentName, documentUrls)
                        .map(documentContent -> buildFile(documentName, documentContent, directory)));
    }

    private Mono<String> getDocumentContent(String documentName, Map<String, String> documentUrls) {
        return Mono.justOrEmpty(documentUrls.get(documentName))
                .flatMap(fileConvertGateway::getFileContentAsBase64)
                .switchIfEmpty(ExceptionFactory.monoBusiness(NOT_GENERATED, documentName));
    }

    private File buildFile(String documentName, String documentContent, String directory) {
        return File.builder()
                .name(documentName)
                .content(documentContent)
                .directoryPath(directory)
                .extension(FileExtensions.PDF)
                .build();
    }

    private List<String> getListDocumentsToGenerate(List<Agreement.Document> documents) {
        return documents.stream()
                .flatMap(document -> document.getFields().stream())
                .map(Agreement.Document.Field::getTechnicalName)
                .distinct()
                .map("csb/"::concat)
                .toList();
    }

}
