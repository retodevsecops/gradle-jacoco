package com.consubanco.api.services.file.handlers;

import com.consubanco.api.commons.util.AttachmentFactoryUtil;
import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.file.dto.AttachmentStatusResDTO;
import com.consubanco.api.services.file.dto.FileResDTO;
import com.consubanco.api.services.file.dto.UploadAttachmentsResDTO;
import com.consubanco.api.services.file.dto.UploadOfficialIdentificationReqDTO;
import com.consubanco.model.entities.file.File;
import com.consubanco.usecase.document.GetPayloadDataUseCase;
import com.consubanco.usecase.file.GetCustomerVisibleFilesUseCase;
import com.consubanco.usecase.file.GetFilesByOfferUseCase;
import com.consubanco.usecase.file.UploadAgreementAttachmentsUseCase;
import com.consubanco.usecase.file.UploadOfficialIDUseCase;
import com.consubanco.usecase.ocr.usecase.GetStatusAttachmentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.consubanco.api.services.file.constants.FileParams.PROCESS_ID;

@Component
@RequiredArgsConstructor
public class OfferFileHandler {

    private final GetFilesByOfferUseCase getFilesByOfferUseCase;
    private final GetCustomerVisibleFilesUseCase getCustomerVisibleFilesUseCase;
    private final UploadAgreementAttachmentsUseCase uploadFilesAgreementUseCase;
    private final GetPayloadDataUseCase getPayloadDataUseCase;
    private final UploadOfficialIDUseCase uploadOfficialIDUseCase;
    private final GetStatusAttachmentUseCase getStatusAttachmentUseCase;

    public Mono<ServerResponse> getFilesOfferByProcess(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return executeUseCase(processId, getFilesByOfferUseCase::execute);
    }

    public Mono<ServerResponse> getCustomerVisibleFiles(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return executeUseCase(processId, getCustomerVisibleFilesUseCase::execute);
    }

    public Mono<ServerResponse> uploadAgreementFiles(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return AttachmentFactoryUtil.extractAttachments(request)
                .flatMap(attachments -> uploadFilesAgreementUseCase.execute(processId, attachments))
                .map(UploadAttachmentsResDTO::new)
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> getPayloadData(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return getPayloadDataUseCase.execute(processId)
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> uploadOfficialID(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return request.bodyToMono(UploadOfficialIdentificationReqDTO.class)
                .flatMap(UploadOfficialIdentificationReqDTO::check)
                .flatMap(dto -> uploadOfficialIDUseCase.execute(processId, dto.front(), dto.back()))
                .map(FileResDTO::new)
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> attachmentStatus(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return getStatusAttachmentUseCase.execute(processId)
                .map(AttachmentStatusResDTO::new)
                .flatMap(HttpResponseUtil::ok);
    }

    private Mono<ServerResponse> executeUseCase(String parameter, Function<String, Flux<File>> useCase) {
        return useCase.apply(parameter)
                .map(FileResDTO::new)
                .collectList()
                .flatMap(HttpResponseUtil::ok);
    }

}
