package com.consubanco.usecase.file;

import com.consubanco.model.entities.document.gateway.DocumentGateway;
import com.consubanco.model.entities.document.vo.GenerateDocumentVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileConvertGateway;
import com.consubanco.model.entities.file.gateway.FileRepository;
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

    public Mono<String> getAsUrl(GenerateDocumentVO generateDocumentVO, String processId) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(buildPayloadUseCase::execute)
                .flatMap(payload -> documentGateway.generate(generateDocumentVO, payload));
    }

    public Mono<String> getAsEncodedFile(GenerateDocumentVO data, String processId) {
        return getAsUrl(data, processId)
                .flatMap(fileConvertGateway::getFileContentAsBase64);
    }

    public Mono<File> getAndUpload(String processId, GenerateDocumentVO generateDocumentVO, String fileName) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(process -> buildPayloadUseCase.execute(process)
                        .flatMap(payload -> documentGateway.generate(generateDocumentVO, payload))
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
