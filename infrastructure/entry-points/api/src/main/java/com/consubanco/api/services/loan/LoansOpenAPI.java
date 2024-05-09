package com.consubanco.api.services.loan;

import com.consubanco.api.commons.swagger.ParamsOpenAPI;
import com.consubanco.api.services.file.constants.FileParams;
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
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, "Process identifier"))
                .response(responseOk(String.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
