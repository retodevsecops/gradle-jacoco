package com.consubanco.api.services.agreement;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;

public class AgreementOpenAPI {

    private static final String TAG = "Agreement";

    public static Consumer<Builder> findByNumber() {
        return ops -> ops.tag(TAG)
                .operationId("agreementFindByNumber")
                .description("Find agreement by number")
                .summary("Find agreement by number")
                .parameter(parameterBuilder()
                        .in(ParameterIn.PATH)
                        .name("agreementNumber")
                        .description("Agreement number"));
    }

}
