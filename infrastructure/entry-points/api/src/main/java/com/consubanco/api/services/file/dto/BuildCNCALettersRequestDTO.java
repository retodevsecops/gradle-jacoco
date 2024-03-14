package com.consubanco.api.services.file.dto;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildCNCALettersRequestDTO {

    @Valid
    @NotNull(message = "Offer is required")
    private OfferDTO offer;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OfferDTO implements Serializable {

        @NotBlank(message = "Offer identifier is required")
        private String id;

        @Valid
        @NotEmpty(message = "List of loans is required")
        private List<@NotNull(message = "LoansId is required") @Positive(message = "LoansId must be numerical") String> loansId;
    }

}
