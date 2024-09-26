package com.consubanco.api.services.rpa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RpaResponseDTO {

    @Schema(description = "HTTP status code.", example = "200", requiredMode = REQUIRED)
    @JsonProperty("status")
    private String status;

    @Schema(description = "Response code for request.", example = "code", requiredMode = REQUIRED)
    @JsonProperty("code")
    private String code;

    @Schema(description = "Message providing additional details about the response.", example = "message", requiredMode = REQUIRED)
    @JsonProperty("message")
    private String message;

    public static RpaResponseDTO buildSuccess() {
        return RpaResponseDTO.builder()
                .status(String.valueOf(HttpStatus.OK.value()))
                .code(String.valueOf(HttpStatus.OK.value()))
                .message("The operation was successful.")
                .build();
    }

}