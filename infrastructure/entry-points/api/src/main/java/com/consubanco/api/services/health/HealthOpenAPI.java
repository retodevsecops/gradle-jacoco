package com.consubanco.api.services.health;

import com.consubanco.api.commons.swagger.ParamsOpenAPI;
import com.consubanco.api.services.agreement.constants.AgreementPathParams;
import com.consubanco.api.services.agreement.dto.AttachmentResDTO;
import com.consubanco.api.services.agreement.dto.GetAgreementResponseDTO;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.*;

public class HealthOpenAPI {

    private static final String TAG = "Health";

    public static Consumer<Builder> health() {
        return ops -> ops.tag(TAG)
                .operationId("health")
                .description("Microservice health test.")
                .summary("Microservice health test.")
                .response(responseOk(String.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
