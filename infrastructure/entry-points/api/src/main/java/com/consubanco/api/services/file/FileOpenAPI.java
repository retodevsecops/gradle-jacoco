package com.consubanco.api.services.file;

import com.consubanco.api.services.file.dto.BuildCNCALettersRequestDTO;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.responseBusinessException;
import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.responseInternalError;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class FileOpenAPI {

    private static final String TAG = "File";

    public static Consumer<Builder> buildCNCALetters() {
        return ops -> ops.tag(TAG)
                .operationId("getCNCALetter")
                .description("Get CNCA letter by account number.")
                .summary("Get CNCA letter.")
                .requestBody(requestBuildCNCALetters())
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    private static org.springdoc.core.fn.builders.requestbody.Builder requestBuildCNCALetters() {
        return requestBodyBuilder()
                .description("Data structure that must go in the body of the request.")
                .required(true)
                .content(contentBuilder()
                        .mediaType(APPLICATION_JSON_VALUE)
                        .schema(schemaBuilder().implementation(BuildCNCALettersRequestDTO.class)));
    }

}
