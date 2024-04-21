package com.consubanco.usecase.file;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileConvertGateway;
import com.consubanco.model.entities.file.gateway.FileGateway;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.vo.FileDataVO;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.document.BuildPayloadUseCase;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
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
    private final GetProcessByIdUseCase getProcessByIdUseCase;

    public Mono<String> getAsUrl(FileDataVO fileData, String processId) {
        return getProcessByIdUseCase.execute(processId)
                .map(Process::getId)
                .flatMap(buildPayloadUseCase::execute)
                .flatMap(payload -> fileGateway.generate(fileData.getDocuments(), fileData.getAttachments(), payload));
    }

    public Mono<String> getAsEncodedFile(FileDataVO fileData, String processId) {
        return getAsUrl(fileData, processId)
                .flatMap(fileConvertGateway::encodedFile);
    }

    public Mono<File> getAndUpload(String processId, FileDataVO fileData, String fileName) {
        return getProcessByIdUseCase.execute(processId)
                .map(Process::getOffer)
                .flatMap(offer -> buildPayloadUseCase.execute(processId)
                        .flatMap(payload -> fileGateway.generate(fileData.getDocuments(), fileData.getAttachments(), payload))
                        .flatMap(fileConvertGateway::encodedFile)
                        .map(encodedFile -> buildFile(offer.getId(), fileName, encodedFile)))
                .flatMap(fileRepository::save);
    }

    private File buildFile(String offerId, String fileName, String encodedFile) {
        return File.builder()
                .name(pdfFormat(fileName))
                .content(encodedFile)
                .directoryPath(documentsDirectory(offerId))
                .extension(FileExtensions.PDF)
                .build();
    }

}
