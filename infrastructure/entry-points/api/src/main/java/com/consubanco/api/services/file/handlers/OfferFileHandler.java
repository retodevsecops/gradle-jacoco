package com.consubanco.api.services.file.handlers;

import com.consubanco.api.commons.util.FilePartUtil;
import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.file.dto.FileResDTO;
import com.consubanco.api.services.file.dto.UploadOfficialIdentificationReqDTO;
import com.consubanco.model.entities.file.File;
import com.consubanco.usecase.document.GetPayloadDataUseCase;
import com.consubanco.usecase.file.GetCustomerVisibleFilesUseCase;
import com.consubanco.usecase.file.GetFilesByOfferUseCase;
import com.consubanco.usecase.file.UploadAgreementFilesUseCase;
import com.consubanco.usecase.file.UploadOfficialIDUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.consubanco.api.services.file.constants.FileParams.OFFER_ID;
import static com.consubanco.api.services.file.constants.FileParams.PROCESS_ID;

@Component
@RequiredArgsConstructor
public class OfferFileHandler {

    private final GetFilesByOfferUseCase getFilesByOfferUseCase;
    private final GetCustomerVisibleFilesUseCase getCustomerVisibleFilesUseCase;
    private final UploadAgreementFilesUseCase uploadFilesAgreementUseCase;
    private final GetPayloadDataUseCase getPayloadDataUseCase;
    private final UploadOfficialIDUseCase uploadOfficialIDUseCase;

    public Mono<ServerResponse> getFilesByOffer(ServerRequest request) {
        return executeUseCase(request.pathVariable(OFFER_ID), getFilesByOfferUseCase::execute);
    }

    public Mono<ServerResponse> getCustomerVisibleFiles(ServerRequest request) {
        return executeUseCase(request.pathVariable(PROCESS_ID), getCustomerVisibleFilesUseCase::execute);
    }

    public Mono<ServerResponse> uploadAgreementFiles(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return request.body(BodyExtractors.toParts())
                .cast(FilePart.class)
                .flatMap(FilePartUtil::buildFileUploadVOFromFilePart)
                .collectList()
                .flatMap(files -> uploadFilesAgreementUseCase.execute(processId, files))
                .flatMap(HttpResponseUtil::accepted);
    }

    public Mono<ServerResponse> getPayloadData(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return getPayloadDataUseCase.execute(processId)
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> uploadOfficialID(ServerRequest request) {
        String processId = request.pathVariable(PROCESS_ID);
        return request.bodyToMono(UploadOfficialIdentificationReqDTO.class)
                .flatMap(UploadOfficialIdentificationReqDTO::check)
                .flatMap(dto -> uploadOfficialIDUseCase.execute(processId, dto.front(), dto.back()))
                .map(FileResDTO::new)
                .flatMap(HttpResponseUtil::Ok);
    }

    private Mono<ServerResponse> executeUseCase(String parameter, Function<String, Flux<File>> useCase) {
        return useCase.apply(parameter)
                .map(FileResDTO::new)
                .collectList()
                .flatMap(HttpResponseUtil::Ok);
    }

}
