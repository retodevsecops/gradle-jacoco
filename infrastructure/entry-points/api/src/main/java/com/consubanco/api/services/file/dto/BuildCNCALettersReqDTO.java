package com.consubanco.api.services.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildCNCALettersReqDTO implements Serializable {

    @Valid
    @NotNull(message = "Offer is required")
    private OfferDTO offer;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OfferDTO implements Serializable {

        @NotBlank(message = "Offer identifier is required")
        @Schema(description = "Offer identification number", example = "1775622", requiredMode = REQUIRED)
        private String id;

        @Valid
        @NotEmpty(message = "List of loans is required")
        @Schema(description = "List of loans belonging to the offer", requiredMode = REQUIRED)
        private List<@NotNull(message = "LoansId is required") @Positive(message = "LoansId must be numerical") String> loansId;
    }

}
