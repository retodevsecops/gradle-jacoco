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
    private List<Catalog> frequencySalary;
    private String signatureColor;
    private List<Catalog> employeeType;
    private List<Catalog> quotationType;
    private List<Catalog> contract_type;
    private List<Catalog> positions;
    private Boolean videoTaskIsRequired;
    private List<Document> documents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Catalog {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Document {
        private String name;
        private String technicalName;
        private Boolean isRequired;
        private String type;
        private String max;
        private List<String> typeFile;
        private List<Field> fields;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Field {
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
