package com.consubanco.api.services.management;

import com.consubanco.api.services.file.dto.GenerateDocumentResDTO;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.*;

@UtilityClass
public class ManagementOpenAPI {

    private static final String TAG = "Management";

    public static Consumer<Builder> cleanCache() {
        return ops -> ops.tag(TAG)
                .operationId("cleanCache")
                .description("Clean microservice cache.")
                .summary("Clean microservice cache.")
                .response(responseOk(GenerateDocumentResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> getItemsCache() {
        return ops -> ops.tag(TAG)
                .operationId("getItemsCache")
                .description("Get Items of cache.")
                .summary("Get Items of cache.")
                .response(responseOkWithList(String.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
