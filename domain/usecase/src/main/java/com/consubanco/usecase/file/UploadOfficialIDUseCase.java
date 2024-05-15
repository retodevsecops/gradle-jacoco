package com.consubanco.usecase.file;

import com.consubanco.model.entities.document.constant.DocumentNames;
import com.consubanco.model.entities.document.gateway.PDFDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileConvertGateway;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.List;

import static com.consubanco.model.entities.file.constant.FileConstants.attachmentsDirectory;

@RequiredArgsConstructor
public class UploadOfficialIDUseCase {

    private final FileRepository fileRepository;
    private final GetProcessByIdUseCase getProcessById;
    private final PDFDocumentGateway pdfDocumentGateway;
    private final FileConvertGateway fileConvert;

    public Mono<File> execute(String processId, String urlFrontOfficialID, String urlBackOfficialID) {
        Mono<String> frontOfficialID = fileConvert.getFileContentAsBase64(urlFrontOfficialID);
        Mono<String> backOfficialID = fileConvert.getFileContentAsBase64(urlBackOfficialID);
        return Mono.zip(getProcessById.execute(processId), frontOfficialID, backOfficialID)
                .flatMap(TupleUtils.function(this::buildFile))
                .flatMap(fileRepository::saveWithSignedUrl);

    }

    private Mono<File> buildFile(Process process, String frontOfficialID, String backOfficialID) {
        return pdfDocumentGateway.generatePdfWithImages(List.of(frontOfficialID, backOfficialID))
                .map(fileContent -> File.builder()
                        .name(DocumentNames.OFFICIAL_ID)
                        .content(fileContent)
                        .extension(FileExtensions.PDF)
                        .directoryPath(attachmentsDirectory(process.getOffer().getId()))
                        .build());
    }

}
