package com.consubanco.api.services.loans;

import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.*;

public class LoansOpenAPI {

    private static final String TAG = "Loans";

    public static Consumer<Builder> createApplication() {
        return ops -> ops.tag(TAG)
                .operationId("createApplication")
                .description("Create loan application.")
                .summary("Create loan application.")
                .response(responseOk(String.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
