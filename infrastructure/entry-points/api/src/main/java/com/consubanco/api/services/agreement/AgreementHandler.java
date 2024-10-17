package com.consubanco.api.services.agreement;

import com.consubanco.api.commons.util.FilePartUtil;
import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.agreement.constants.AgreementPathParams;
import com.consubanco.api.services.agreement.dto.AttachmentResDTO;
import com.consubanco.api.services.agreement.dto.GetAgreementResponseDTO;
import com.consubanco.usecase.agreement.AgreementUseCase;
import com.consubanco.usecase.agreement.GenerateAgreementDocumentsUseCase;
import com.consubanco.usecase.agreement.GetAttachmentsByAgreementUseCase;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AgreementHandler {

    private static final Map RESPONSE_SUCCESS = Map.of("message", "all agreement documents were generated.");
    private final AgreementUseCase agreementUseCase;
    private final GetAttachmentsByAgreementUseCase getAttachmentsByAgreementUseCase;
    private final ModelMapper mapper;
    private final GenerateAgreementDocumentsUseCase generateAgreementDocumentsUseCase;

    public Mono<ServerResponse> findByNumber(ServerRequest serverRequest) {
        String agreementNumber = serverRequest.pathVariable(AgreementPathParams.AGREEMENT_NUMBER);
        return agreementUseCase.findByNumber(agreementNumber)
                .map(agreement -> mapper.map(agreement, GetAgreementResponseDTO.class))
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> getAttachments(ServerRequest serverRequest) {
        return Mono.just(AgreementPathParams.PROCESS_ID)
                .map(serverRequest::pathVariable)
                .flatMapMany(getAttachmentsByAgreementUseCase::execute)
                .map(attachment -> mapper.map(attachment, AttachmentResDTO.class))
                .collectList()
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> generateDocuments(ServerRequest serverRequest) {
        String processId = serverRequest.pathVariable(AgreementPathParams.PROCESS_ID);
        return serverRequest.body(BodyExtractors.toParts())
                .ofType(FilePart.class)
                .next()
                .flatMap(FilePartUtil::buildFileUploadVOFromFilePart)
                .flatMap(fileUploadVO -> generateAgreementDocumentsUseCase.execute(processId, fileUploadVO))
                .thenReturn(RESPONSE_SUCCESS)
                .flatMap(HttpResponseUtil::ok);
    }

}
