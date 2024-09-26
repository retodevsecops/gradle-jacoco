package com.consubanco.api.services.rpa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Response DTO for Send Carta Libranza operation")
public class UploadCartaLibranzaResDTO {

    @Schema(description = "Response data for Send Carta Libranza.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("sendCartaLibranzaResBO")
    private RpaResponseDTO data;

    public UploadCartaLibranzaResDTO() {
        this.data = RpaResponseDTO.buildSuccess();
    }
}
