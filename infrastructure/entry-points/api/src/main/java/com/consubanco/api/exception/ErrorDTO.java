package com.consubanco.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ErrorDTO {

    @Schema(description = "Error identifier")
    private String code;


    @Schema(description = "Message of the error")
    private String message;

    @Schema(description = "Technical detail of the error")
    private String reason;

    @Schema(description = "Service where the error occurred")
    private String domain;

}
