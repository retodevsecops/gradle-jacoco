package com.consubanco.usecase.file;

import com.consubanco.model.entities.document.gateway.DocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileConvertGateway;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.vo.FileDataVO;
import com.consubanco.usecase.document.BuildPayloadDocumentUseCase;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.file.constant.FileConstants.documentsDirectory;
import static com.consubanco.model.entities.file.constant.FileConstants.pdfFormat;

@RequiredArgsConstructor
public class GenerateDocumentUseCase {

    private final BuildPayloadDocumentUseCase buildPayloadUseCase;
    private final DocumentGateway documentGateway;
    private final FileConvertGateway fileConvertGateway;
    private final FileRepository fileRepository;
    private final GetProcessByIdUseCase getProcessByIdUseCase;

    public Mono<String> getAsUrl(FileDataVO fileData, String processId) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(buildPayloadUseCase::execute)
                .flatMap(payload -> documentGateway.generate(fileData.getDocuments(), fileData.getAttachments(), payload));
    }

    public Mono<String> getAsEncodedFile(FileDataVO fileData, String processId) {
        return getAsUrl(fileData, processId)
                .flatMap(fileConvertGateway::getFileContentAsBase64);
    }

    public Mono<File> getAndUpload(String processId, FileDataVO fileData, String fileName) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(process -> buildPayloadUseCase.execute(process)
                        .flatMap(payload -> documentGateway.generate(fileData.getDocuments(), fileData.getAttachments(), payload))
                        .flatMap(fileConvertGateway::getFileContentAsBase64)
                        .map(encodedFile -> buildFile(process.getOfferId(), fileName, encodedFile)))
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
