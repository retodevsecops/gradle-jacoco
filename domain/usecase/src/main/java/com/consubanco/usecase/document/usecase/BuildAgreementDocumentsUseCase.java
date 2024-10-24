package com.consubanco.usecase.document.usecase;

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

    public Flux<File> execute(Process process, Agreement agreement) {
        String directory = FileConstants.documentsDirectory(process.getOffer().getId());
        return buildPayloadUseCase.execute(process)
                .flatMap(payload -> generateDocuments(agreement, payload))
                .flatMapMany(documentUrlsMap -> generateFilesFromUrls(documentUrlsMap, agreement, directory))
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(fileRepository::save)
                .sequential();
    }

    private Mono<Map<String, String>> generateDocuments(Agreement agreement, Map<String, Object> payload) {
        List<String> documentsToGenerate = agreement.getDocumentsToGenerate();
        if (agreement.isMN()) return documentGateway.generateMultipleMN(documentsToGenerate, payload);
        return documentGateway.generateMultiple(documentsToGenerate, payload);
    }

    private Flux<File> generateFilesFromUrls(Map<String, String> documentUrls,
                                             Agreement agreement,
                                             String directory) {
        return Flux.fromIterable(agreement.getDocumentsToGenerate())
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

}
