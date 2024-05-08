package com.consubanco.usecase.file;

import com.consubanco.model.entities.document.constant.DocumentNames;
import com.consubanco.model.entities.document.gateway.PDFDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.List;

import static com.consubanco.model.entities.file.constant.FileConstants.attachmentsDirectory;
import static com.consubanco.model.entities.file.util.AttachmentValidatorUtil.checkFileSize;

@RequiredArgsConstructor
public class UploadOfficialIDUseCase {

    private final FileRepository fileRepository;
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final PDFDocumentGateway pdfDocumentGateway;

    public Mono<File> execute(String processId, FileUploadVO fileUploadVO) {
        return Mono.zip(getProcessByIdUseCase.execute(processId), checkFileUpload(fileUploadVO))
                .flatMap(TupleUtils.function(this::buildFile))
                .flatMap(fileRepository::save);

    }

    private Mono<FileUploadVO> checkFileUpload(FileUploadVO fileUploadVO) {
        return fileUploadVO.check()
                .flatMap(data -> checkFileSize(data, fileRepository.getMaxSizeOfFileInMBAllowed()));
    }

    private Mono<File> buildFile(Process process, FileUploadVO fileUploadVO) {
        return convertToPDF(fileUploadVO)
                .map(fileContent -> File.builder()
                        .name(DocumentNames.OFFICIAL_ID)
                        .content(fileContent)
                        .extension(FileExtensions.PDF)
                        .directoryPath(attachmentsDirectory(process.getOffer().getId()))
                        .build());
    }

    private Mono<String> convertToPDF(FileUploadVO fileUploadVO) {
        return Mono.just(fileUploadVO)
                .filter(FileUploadVO::isNotPDF)
                .map(FileUploadVO::getContent)
                .map(List::of)
                .flatMap(pdfDocumentGateway::generatePdfWithImages)
                .defaultIfEmpty(fileUploadVO.getContent());
    }

}
