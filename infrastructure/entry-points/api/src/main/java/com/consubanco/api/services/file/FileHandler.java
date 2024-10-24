package com.consubanco.api.services.file;

import com.consubanco.api.commons.util.FilePartUtil;
import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.file.dto.*;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.consubanco.usecase.agreement.GetAttachmentsByAgreementUseCase;
import com.consubanco.usecase.document.usecase.BuildCNCALettersUseCase;
import com.consubanco.usecase.file.FileUseCase;
import com.consubanco.usecase.file.GenerateDocumentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.function.Function;

import static com.consubanco.api.services.file.constants.FileParams.PROCESS_ID;

@Component
@RequiredArgsConstructor
public class FileHandler {

    private final FileUseCase fileUseCase;
    private final BuildCNCALettersUseCase buildCNCALettersUseCase;
    private final GenerateDocumentUseCase generateDocUseCase;
    private final GetAttachmentsByAgreementUseCase getAttachmentsByAgreementUseCase;

    public Mono<ServerResponse> buildCNCALetters(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        loadDocumentsFromPreviousApplication(processId);
        return buildCNCALettersUseCase.execute(processId)
                .map(FileResDTO::new)
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> generateFileWithDocuments(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return request.bodyToMono(GenerateDocumentReqDTO.class)
                .map(GenerateDocumentReqDTO::buildGenerateDocumentVO)
                .flatMap(req -> generateDocUseCase.getAsUrl(req, processId))
                .map(GenerateDocumentResDTO::new)
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> generateFileEncoded(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return request.bodyToMono(GenerateDocumentReqDTO.class)
                .map(GenerateDocumentReqDTO::buildGenerateDocumentVO)
                .flatMap(req -> generateDocUseCase.getAsEncodedFile(req, processId))
                .map(GenerateDocumentResDTO::new)
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> getAndUpload(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return request.bodyToMono(GetAndUploadDocumentReqDTO.class)
                .flatMap(req -> generateDocUseCase.getAndUpload(processId, req.getData(), req.getFileName()))
                .map(FileResDTO::new)
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> uploadPayloadTemplate(ServerRequest request) {
        return processUploadCase(request, fileUseCase::uploadPayloadTemplate);
    }

    public Mono<ServerResponse> uploadAgreementsConfig(ServerRequest request) {
        return processUploadCase(request, fileUseCase::uploadAgreementsConfig);
    }

    public Mono<ServerResponse> uploadCreateApplicationTemplate(ServerRequest request) {
        return processUploadCase(request, fileUseCase::uploadCreateApplicationTemplate);
    }

    public Mono<ServerResponse> getManagementFiles(ServerRequest request) {
        return fileUseCase.getManagementFiles()
                .map(FileResDTO::new)
                .collectList()
                .flatMap(HttpResponseUtil::ok);
    }

    public  Mono<ServerResponse> validateTemplate(ServerRequest request) {
        return request.bodyToMono(ValidateTemplateReqDTO.class)
                .flatMap(req -> fileUseCase.validateTemplate(req.getTemplate(), req.getData()))
                .flatMap(HttpResponseUtil::ok);
    }

    private Mono<ServerResponse> processUploadCase(ServerRequest request, Function<FileUploadVO, Mono<File>> useCase) {
        return request.body(BodyExtractors.toParts())
                .ofType(FilePart.class)
                .next()
                .flatMap(FilePartUtil::buildFileUploadVOFromFilePart)
                .flatMap(useCase)
                .map(FileResDTO::new)
                .flatMap(HttpResponseUtil::ok);
    }

    private void loadDocumentsFromPreviousApplication(String processId) {
        getAttachmentsByAgreementUseCase.execute(processId)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

}
