package com.consubanco.api.services.agreement;

import com.consubanco.api.services.agreement.dto.GetAgreementResponseDTO;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.*;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;

public class AgreementOpenAPI {

    private static final String TAG = "Agreement";

    public static Consumer<Builder> findByNumber() {
        return ops -> ops.tag(TAG)
                .operationId("agreementFindByNumber.")
                .description("Find agreement by number.")
                .summary("Find agreement by number.")
                .parameter(agreementNumberPathParam())
                .response(responseOk(GetAgreementResponseDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    private static org.springdoc.core.fn.builders.parameter.Builder agreementNumberPathParam() {
        return parameterBuilder()
                .in(ParameterIn.PATH)
                .name("agreementNumber")
                .description("Agreement number");
    }

}
