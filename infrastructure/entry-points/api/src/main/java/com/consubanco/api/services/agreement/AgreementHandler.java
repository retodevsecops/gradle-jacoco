package com.consubanco.api.services.agreement;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.agreement.constants.AgreementPathParams;
import com.consubanco.api.services.agreement.dto.AttachmentResDTO;
import com.consubanco.api.services.agreement.dto.GetAgreementResponseDTO;
import com.consubanco.usecase.agreement.AgreementUseCase;
import com.consubanco.usecase.agreement.GetAttachmentsByAgreementUseCase;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AgreementHandler {

    private final AgreementUseCase agreementUseCase;
    private final GetAttachmentsByAgreementUseCase getAttachmentsByAgreementUseCase;
    private final ModelMapper mapper;

    public Mono<ServerResponse> findByNumber(ServerRequest serverRequest) {
        String agreementNumber = serverRequest.pathVariable(AgreementPathParams.AGREEMENT_NUMBER);
        return agreementUseCase.findByNumber(agreementNumber)
                .map(agreement -> mapper.map(agreement, GetAgreementResponseDTO.class))
                .flatMap(HttpResponseUtil::Ok);
    }

    public Mono<ServerResponse> getAttachments(ServerRequest serverRequest) {
        return Mono.just(AgreementPathParams.PROCESS_ID)
                .map(serverRequest::pathVariable)
                .flatMapMany(getAttachmentsByAgreementUseCase::execute)
                .doOnNext(e -> System.out.println(e.toString()))
                .map(attachment -> mapper.map(attachment, AttachmentResDTO.class))
                .collectList()
                .flatMap(HttpResponseUtil::Ok);
    }

}
