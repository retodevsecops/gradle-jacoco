package com.consubanco.api.services.file;

import com.consubanco.api.commons.util.FilePartUtil;
import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.agreement.constants.AgreementPathParams;
import com.consubanco.api.services.file.constants.FilePathParams;
import com.consubanco.api.services.file.dto.*;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.usecase.file.BuildCNCALettersUseCase;
import com.consubanco.usecase.file.FileUseCase;
import com.consubanco.usecase.file.GenerateDocumentUseCase;
import com.consubanco.usecase.file.UploadAgreementFilesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.consubanco.api.services.file.constants.FilePathParams.*;

@Component
@RequiredArgsConstructor
public class FileHandler {

    private final FileUseCase fileUseCase;
    private final BuildCNCALettersUseCase buildCNCALettersUseCase;
    private final GenerateDocumentUseCase generateDocUseCase;
    private final UploadAgreementFilesUseCase uploadFilesAgreementUseCase;

    public Mono<ServerResponse> buildCNCALetters(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return buildCNCALettersUseCase.execute(processId)
                .map(FileResDTO::new)
                .collectList()
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> generateFileWithDocuments(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return request.bodyToMono(GenerateDocumentReqDTO.class)
                .map(GenerateDocumentReqDTO::buildFileDataVO)
                .flatMap(req -> generateDocUseCase.getAsUrl(req, processId))
                .map(GenerateDocumentResDTO::new)
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> generateFileEncoded(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return request.bodyToMono(GenerateDocumentReqDTO.class)
                .map(GenerateDocumentReqDTO::buildFileDataVO)
                .flatMap(req -> generateDocUseCase.getAsEncodedFile(req, processId))
                .map(GenerateDocumentResDTO::new)
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> getAndUpload(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return request.bodyToMono(GetAndUploadDocumentReqDTO.class)
                .flatMap(req -> generateDocUseCase.getAndUpload(processId, req.getData(), req.getFileName()))
                .map(FileResDTO::new)
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> uploadAgreementFiles(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return request.body(BodyExtractors.toParts())
                .cast(FilePart.class)
                .flatMap(this::buildFileUploadVO)
                .collectList()
                .flatMap(files -> uploadFilesAgreementUseCase.execute(processId, files))
                .flatMap(HttpResponseUtil::accepted);
    }

    public Mono<ServerResponse> getFilesByOffer(ServerRequest request) {
        String offerId = request.pathVariable(FilePathParams.OFFER_ID);
        return fileUseCase.getFilesByOffer(offerId)
                .map(FileResDTO::new)
                .collectList()
                .flatMap(HttpResponseUtil::Ok);
    }

    private Mono<FileUploadVO> buildFileUploadVO(FilePart filePart) {
        return FilePartUtil.fileToBase64(filePart)
                .map(fileInBase63 -> FileUploadVO.builder()
                        .name(filePart.name())
                        .extension(StringUtils.getFilenameExtension(filePart.filename()))
                        .content(fileInBase63)
                        .build());
    }

    public Mono<ServerResponse> uploadPayloadTemplate(ServerRequest request) {
        return request.body(BodyExtractors.toParts())
                .ofType(FilePart.class)
                .next()
                .flatMap(FilePartUtil::fileToBase64)
                .flatMap(fileUseCase::uploadPayloadTemplate)
                .map(FileResDTO::new)
                .flatMap(HttpResponseUtil::Ok);
    }

}
