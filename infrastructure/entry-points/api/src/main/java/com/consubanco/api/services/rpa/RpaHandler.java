package com.consubanco.api.services.rpa;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.rpa.dto.UploadCartaLibranzaReqDTO;
import com.consubanco.api.services.rpa.dto.UploadCartaLibranzaResDTO;
import com.consubanco.api.services.rpa.dto.UploadSipreSimulationReqDTO;
import com.consubanco.api.services.rpa.dto.UploadSipreSimulationResDTO;
import com.consubanco.usecase.rpa.UploadCartaLibranzaUseCase;
import com.consubanco.usecase.rpa.UploadSipreSimulationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RpaHandler {

    private final UploadCartaLibranzaUseCase uploadCartaLibranzaUseCase;
    private final UploadSipreSimulationUseCase uploadSipreSimulationUseCase;

    public Mono<ServerResponse> uploadCartaLibranza(ServerRequest request) {
        return request.bodyToMono(UploadCartaLibranzaReqDTO.class)
                .map(UploadCartaLibranzaReqDTO::toModel)
                .flatMap(uploadCartaLibranzaUseCase::execute)
                .thenReturn(new UploadCartaLibranzaResDTO())
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> uploadSipreSimulation(ServerRequest request) {
        return request.bodyToMono(UploadSipreSimulationReqDTO.class)
                .map(UploadSipreSimulationReqDTO::toModel)
                .flatMap(uploadSipreSimulationUseCase::execute)
                .thenReturn(new UploadSipreSimulationResDTO())
                .flatMap(HttpResponseUtil::ok);
    }

}
