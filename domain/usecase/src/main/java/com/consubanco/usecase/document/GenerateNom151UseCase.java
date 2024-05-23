package com.consubanco.usecase.document;

import com.consubanco.model.entities.document.constant.DocumentNames;
import com.consubanco.model.entities.document.gateway.DocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.file.constant.FileConstants.documentsDirectory;

@RequiredArgsConstructor
public class GenerateNom151UseCase {

    private final DocumentGateway documentGateway;
    private final FileRepository fileRepository;

    public Mono<File> execute(File file, Process process) {
        return Mono.zip(file.checkRequiredData(), process.checkRequiredData())
                .flatMap(tuple -> documentGateway.generateNom151(file.contentDecode(), file.getName()))
                .map(nom151 -> File.builder()
                        .name(DocumentNames.documentNameWithNom151(file.getName()))
                        .content(nom151)
                        .directoryPath(documentsDirectory(process.getOffer().getId()))
                        .extension(FileExtensions.PDF)
                        .build())
                .flatMap(fileRepository::save);
    }

}
