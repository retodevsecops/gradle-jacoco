package com.consubanco.api.services.rpa;

import com.consubanco.api.commons.swagger.RequestsOpenAPI;
import com.consubanco.api.services.rpa.dto.UploadCartaLibranzaReqDTO;
import com.consubanco.api.services.rpa.dto.UploadCartaLibranzaResDTO;
import com.consubanco.api.services.rpa.dto.UploadSipreSimulationReqDTO;
import com.consubanco.api.services.rpa.dto.UploadSipreSimulationResDTO;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.*;

@UtilityClass
public class RpaOpenAPI {

    private static final String TAG_RPA = "Rpa Documents";

    public static Consumer<Builder> uploadCartaLibranza() {
        return ops -> ops.tag(TAG_RPA)
                .operationId("uploadCartaLibranza")
                .description("Upload carta libranza.")
                .summary("Upload carta libranza.")
                .requestBody(RequestsOpenAPI.body(UploadCartaLibranzaReqDTO.class))
                .response(responseOk(UploadCartaLibranzaResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> uploadSipreSimulation() {
        return ops -> ops.tag(TAG_RPA)
                .operationId("uploadSipreSimulation")
                .description("Upload sipre simulation.")
                .summary("Upload sipre simulation.")
                .requestBody(RequestsOpenAPI.body(UploadSipreSimulationReqDTO.class))
                .response(responseOk(UploadSipreSimulationResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
