package com.consubanco.api.services.loan;

import com.consubanco.api.commons.swagger.ParamsOpenAPI;
import com.consubanco.api.services.file.constants.FileParams;
import com.consubanco.api.services.loan.constants.LoanHeaderParams;
import com.consubanco.model.entities.loan.LoanApplication;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.Map;
import java.util.function.Consumer;

import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.*;

@UtilityClass
public class LoansOpenAPI {

    private static final String TAG = "Loans";
    private static final String PROCESS_ID_DESCRIPTION = "Process identifier";

    public static Consumer<Builder> createApplication() {
        return ops -> ops.tag(TAG)
                .operationId("createApplication")
                .description("Create loan application.")
                .summary("Create loan application.")
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, PROCESS_ID_DESCRIPTION))
                .parameter(ParamsOpenAPI.header(LoanHeaderParams.OTP, "Otp code"))
                .parameter(ParamsOpenAPI.header(LoanHeaderParams.LATITUDE, "Latitude"))
                .parameter(ParamsOpenAPI.header(LoanHeaderParams.LONGITUDE, "Longitude"))
                .parameter(ParamsOpenAPI.header(LoanHeaderParams.IP, "Ip"))
                .parameter(ParamsOpenAPI.header(LoanHeaderParams.USER_AGENT, "User agent"))
                .response(responseOk(String.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> listByProcess() {
        return ops -> ops.tag(TAG)
                .operationId("listByProcess")
                .description("List loan applications by process.")
                .summary("List loan applications by process.")
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, PROCESS_ID_DESCRIPTION))
                .response(responseOk(LoanApplication.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> applicationData() {
        return ops -> ops.tag(TAG)
                .operationId("applicationData")
                .description("Consult all the available data for the loan application.")
                .summary("Get all data for the loan application.")
                .parameter(ParamsOpenAPI.path(FileParams.PROCESS_ID, PROCESS_ID_DESCRIPTION))
                .response(responseOk(Map.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
