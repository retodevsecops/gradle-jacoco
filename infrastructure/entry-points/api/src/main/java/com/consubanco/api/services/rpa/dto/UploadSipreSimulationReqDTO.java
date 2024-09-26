package com.consubanco.api.services.rpa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UploadSipreSimulationReqDTO {

    @JsonProperty("simulacionSipreResultReqBO")
    @Schema(description = "Data from the sipre simulation result.", requiredMode = REQUIRED)
    private ApplicationData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "Application data containing customer and document information.")
    public static class ApplicationData {

        @Schema(description = "Unique ID for the application.", example = "CSB-RENEX", requiredMode = REQUIRED)
        private String applicationId;

        @Schema(description = "Channel through which the application was made.", example = "RENEX", requiredMode = REQUIRED)
        private String channel;

        @JsonProperty("reference")
        @Schema(description = "Reference ID for the offer.", example = "223345", requiredMode = REQUIRED)
        private String offerId;

        @Schema(description = "Business partner ID for the customer.", example = "65426535", requiredMode = REQUIRED)
        private String customerBp;

        @JsonProperty("folioNegocio")
        @Schema(description = "Business folio for the 'Carta Libranza'.", example = "09876543", requiredMode = REQUIRED)
        private String folioBusiness;

        @Schema(description = "List of files.", requiredMode = REQUIRED)
        private File files;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "Document details including name and base64 encoded content.")
    public static class File {

        @Schema(description = "Name of the document.", example = "document-credentials", requiredMode = REQUIRED)
        private String name;

        @JsonProperty("base64")
        @Schema(description = "Base64 encoded content of the document.", example = "RXN0byBlcyB1biBlamVtcGxvIGRlIHVuYSBjb2RpZmljYWNpb24gZW4gYmFzZTY0", requiredMode = REQUIRED)
        private String base64Content;
    }

}