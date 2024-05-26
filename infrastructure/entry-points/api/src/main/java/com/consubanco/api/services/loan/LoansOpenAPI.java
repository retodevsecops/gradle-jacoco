package com.consubanco.api.services.loan;

import com.consubanco.api.commons.swagger.ParamsOpenAPI;
import com.consubanco.api.services.file.constants.FileParams;
import com.consubanco.model.entities.loan.LoanApplication;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.Map;
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

    public static Consumer<Builder> listByProcess() {
        return ops -> ops.tag(TAG)
                .operationId("listByProcess")
                .description("List loan applications by process.")
                .summary("List loan applications by process.")
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, "Process identifier"))
                .response(responseOk(LoanApplication.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> applicationData() {
        return ops -> ops.tag(TAG)
                .operationId("applicationData")
                .description("Consult all the available data for the loan application.")
                .summary("Get all data for the loan application.")
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, "Process identifier"))
                .response(responseOk(Map.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
