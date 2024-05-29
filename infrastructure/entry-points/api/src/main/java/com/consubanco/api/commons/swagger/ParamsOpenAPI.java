package com.consubanco.api.commons.swagger;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.parameter.Builder;

import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;

@UtilityClass
public class ParamsOpenAPI {

    public static Builder path(String name, String description) {
        return parameterBuilder()
                .in(ParameterIn.PATH)
                .name(name)
                .description(description);
    }

    public static Builder query(String name, String description) {
        return parameterBuilder()
                .in(ParameterIn.QUERY)
                .name(name)
                .description(description);
    }

    public static Builder header(String name, String description) {
        return parameterBuilder()
                .in(ParameterIn.HEADER)
                .name(name)
                .description(description);
    }

}
