package com.consubanco.api.services.rpa;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.rpa.dto.UploadCartaLibranzaReqDTO;
import com.consubanco.api.services.rpa.dto.UploadCartaLibranzaResDTO;
import com.consubanco.api.services.rpa.dto.UploadSipreSimulationReqDTO;
import com.consubanco.api.services.rpa.dto.UploadSipreSimulationResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RpaHandler {

    public Mono<ServerResponse> uploadCartaLibranza(ServerRequest request) {
        return request.bodyToMono(UploadCartaLibranzaReqDTO.class)
                .map(dto -> new UploadCartaLibranzaResDTO())
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> uploadSipreSimulation(ServerRequest request) {
        return request.bodyToMono(UploadSipreSimulationReqDTO.class)
                .map(dto -> new UploadSipreSimulationResDTO())
                .flatMap(HttpResponseUtil::ok);
    }

}
