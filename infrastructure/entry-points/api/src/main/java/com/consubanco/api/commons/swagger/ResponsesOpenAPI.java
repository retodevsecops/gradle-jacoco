package com.consubanco.api.commons.swagger;

import com.consubanco.api.exception.ErrorDTO;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.apiresponse.Builder;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springframework.http.HttpStatus.*;

@UtilityClass
public class ResponsesOpenAPI {

    public static Builder responseBusinessException() {
        return responseBuilder()
                .responseCode(String.valueOf(CONFLICT.value()))
                .description("Response in case a business rule violation occurs.")
                .implementation(ErrorDTO.class);
    }

    public static Builder responseInternalError() {
        return responseBuilder()
                .responseCode(String.valueOf(INTERNAL_SERVER_ERROR.value()))
                .description("Response in case a internal server error.")
                .implementation(ErrorDTO.class);
    }

    public static <T> Builder responseOk(Class<T> tClass) {
        return responseBuilder()
                .responseCode(String.valueOf(OK.value()))
                .description("Successful response with the data.")
                .implementation(tClass);
    }

}
