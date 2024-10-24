package com.consubanco.api.commons.swagger;

import lombok.experimental.UtilityClass;

import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@UtilityClass
public class RequestsOpenAPI {

    public static <T> org.springdoc.core.fn.builders.requestbody.Builder body(Class<T> tClass) {
        return requestBodyBuilder()
                .description("Data structure that must go in the body of the request.")
                .required(true)
                .content(contentBuilder()
                        .mediaType(APPLICATION_JSON_VALUE)
                        .schema(schemaBuilder().implementation(tClass)));
    }

    public static <T> org.springdoc.core.fn.builders.requestbody.Builder formData() {
        return requestBodyBuilder()
                .description("Data in the request.")
                .required(true)
                .content(contentBuilder()
                        .mediaType(MULTIPART_FORM_DATA_VALUE)
                        .schema(schemaBuilder()
                                .type("object")));
    }

}
