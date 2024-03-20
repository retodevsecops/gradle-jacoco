package com.consubanco.api.services.document;

import com.consubanco.api.commons.swagger.RequestsOpenAPI;
import com.consubanco.api.services.document.dto.GenerateDocumentRequestDTO;
import com.consubanco.api.services.document.dto.GenerateDocumentResponseDTO;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.*;

public class DocumentOpenAPI {

    private static final String TAG = "Document";

    public static Consumer<Builder> generateDocument() {
        return ops -> ops.tag(TAG)
                .operationId("generateDocument.")
                .description("Generate a single PDF document from a list documents.")
                .summary("Generate PDF document.")
                .requestBody(RequestsOpenAPI.body(GenerateDocumentRequestDTO.class))
                .response(responseOk(GenerateDocumentResponseDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
