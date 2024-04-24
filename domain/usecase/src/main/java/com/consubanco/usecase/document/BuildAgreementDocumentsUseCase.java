package com.consubanco.usecase.document;

import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileConvertGateway;
import com.consubanco.model.entities.file.gateway.FileGateway;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.util.FileUtil;
import com.consubanco.model.entities.process.Process;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

import static com.consubanco.model.entities.file.constant.FileConstants.documentsDirectory;

@RequiredArgsConstructor
public class BuildAgreementDocumentsUseCase {

    private final BuildPayloadUseCase buildPayloadUseCase;
    private final FileRepository fileRepository;
    private final FileGateway fileGateway;
    private final FileConvertGateway fileConvertGateway;

    public Flux<File> execute(Process process, List<Agreement.Document> documents) {
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
