package com.consubanco.api.services.management;

import com.consubanco.api.commons.swagger.ParamsOpenAPI;
import com.consubanco.api.services.management.constants.ManagementParams;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.Map;
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
                .response(responseOk(Map.class))
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

    public static Consumer<Builder> objectsByItem() {
        return ops -> ops.tag(TAG)
                .operationId("objectsByItem")
                .description("Get all objects by item.")
                .summary("Get objects by item.")
                .parameter(ParamsOpenAPI.path(ManagementParams.ITEM, "item"))
                .response(responseOkWithList(String.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> cleanByItem() {
        return ops -> ops.tag(TAG)
                .operationId("cleanByItem")
                .description("Clean microservice cache by item.")
                .summary("Clean microservice cache by item.")
                .parameter(ParamsOpenAPI.path(ManagementParams.ITEM, "item"))
                .response(responseOk(Map.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
