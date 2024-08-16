package com.consubanco.api.services.file.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ValidateTemplateReqDTO {

    @Schema(description = "name of the template to be tested or the complete template", example = "payload | create-application | {template}", requiredMode = REQUIRED)
    private String template;

    @Schema(description = "Data for validate template", example = "{}", requiredMode = REQUIRED)
    private Map<String, Object> data;

}
