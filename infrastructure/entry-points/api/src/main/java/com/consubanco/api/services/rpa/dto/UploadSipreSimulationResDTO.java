package com.consubanco.api.services.rpa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UploadSipreSimulationResDTO {

    @JsonProperty("simulacionSipreResultResBO")
    @Schema(description = "Response data for the SIPRE Simulation Result.", requiredMode = REQUIRED)
    private RpaResponseDTO data;

    public UploadSipreSimulationResDTO() {
        this.data = RpaResponseDTO.buildSuccess();
    }
}