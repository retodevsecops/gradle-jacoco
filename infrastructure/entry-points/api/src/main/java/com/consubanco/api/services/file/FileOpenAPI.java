package com.consubanco.api.services.file;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;

public class FileOpenAPI {

    private static final String TAG = "File";

    public static Consumer<Builder> buildCNCALetters() {
        return ops -> ops.tag(TAG)
                .operationId("getCNCALetter")
                .description("Get CNCA letter by account number")
                .summary("Get CNCA letter")
                .parameter(parameterBuilder()
                        .in(ParameterIn.PATH)
                        .name("accountNumber")
                        .description("Account number"));
    }

}
