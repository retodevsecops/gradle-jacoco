package com.consubanco.api.services.agreement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetAgreementResponseDTO {

    @Schema(description = "Agreement identifier", example = "174")
    private String id;

    @Schema(description = "Agreement number", example = "10000208")
    private String number;

    private String name;
    private String businessName;
    private String sectorCode;
    private String sectorCodeType;
    private String calculationBaseCode;
    private String amortizationType;
    private String calculator;
    private String providerCapacity;
    private String csbCode;
    private String csbName;
    private String company;
    private Boolean signaturePromoterIsRequired;
    private List<CatalogDTO> frequencySalary;
    private String signatureColor;
    private List<CatalogDTO> employeeType;
    private List<CatalogDTO> quotationType;
    private List<CatalogDTO> contract_type;
    private List<CatalogDTO> positions;
    private Boolean videoTaskIsRequired;

    private List<DocumentDTO> documents;
    private List<DocumentDTO>  annexes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CatalogDTO {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class DocumentDTO {
        private String id;
        private String name;
        private String technicalName;
        private String order;
        private String classification;
        private Boolean isRequired;
        private Boolean isVisible;
        private Boolean isSpecial;
        private String type;
        private String max;
        private List<String> typeFile;
        private Boolean isClient;
        private String convertTo;
        private List<FieldDTO> fields;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class FieldDTO {
        private String id;
        private Integer order;
        private String name;
        private String technicalName;
        private String classification;
        private String type;
        private Boolean isRequired;
        private String max;
        private Boolean isSpecial;
        private String convertTo;
        private String value;
    }

}
