package com.consubanco.usecase.file;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateways.FileConvertGateway;
import com.consubanco.model.entities.file.gateways.FileGateway;
import com.consubanco.model.entities.file.gateways.FileRepository;
import com.consubanco.model.entities.file.vo.FileDataVO;
import com.consubanco.usecase.document.BuildPayloadUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.file.constant.FileConstants.documentsDirectory;
import static com.consubanco.model.entities.file.constant.FileConstants.pdfFormat;

@RequiredArgsConstructor
public class GenerateDocumentUseCase {

    private final BuildPayloadUseCase buildPayloadUseCase;
    private final FileGateway fileGateway;
    private final FileConvertGateway fileConvertGateway;
    private final FileRepository fileRepository;

    public Mono<String> getAsUrl(FileDataVO fileData) {
        return buildPayloadUseCase.execute()
                .flatMap(payload -> fileGateway.generate(fileData.getDocuments(), fileData.getAttachments(), payload));
    }

    public Mono<String> getAsEncodedFile(FileDataVO fileData) {
        return buildPayloadUseCase.execute()
                .flatMap(payload -> fileGateway.generate(fileData.getDocuments(), fileData.getAttachments(), payload))
                .flatMap(fileConvertGateway::encodedFile);
    }

    public Mono<File> getAndUpload(FileDataVO fileData, String offerId, String fileName) {
        return buildPayloadUseCase.execute()
                .flatMap(payload -> fileGateway.generate(fileData.getDocuments(), fileData.getAttachments(), payload))
                .flatMap(fileConvertGateway::encodedFile)
                .map(encodedFile -> buildFile(offerId, fileName, encodedFile))
                .flatMap(fileRepository::save);
    }

    private File buildFile(String offerId, String fileName, String encodedFile) {
        return File.builder()
                .name(pdfFormat(fileName))
                .content(encodedFile)
                .directoryPath(documentsDirectory(offerId))
                .build();
    }

}
