package com.consubanco.consumer.adapters.agreement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetAgreementDetailResponseDTO {

    @JsonProperty("resGetAgreementDetail")
    private DetailDTO detail;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailDTO implements Serializable {

        private String traceId;

        @JsonProperty("msg")
        private String message;

        @JsonProperty("convenio")
        private AgreementDTO agreement;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AgreementDTO implements Serializable {

        private String id;

        @JsonProperty("numero")
        private String number;

        @JsonProperty("nombre")
        private String name;

        @JsonProperty("razon_social")
        private String businessName;

        @JsonProperty("codigo_sector")
        private String sectorCode;

        @JsonProperty("tipo_codigo_sector")
        private String sectorCodeType;

        @JsonProperty("codigo_base_calculo")
        private String calculationBaseCode;

        @JsonProperty("tipo_amortizacion")
        private String amortizationType;

        @JsonProperty("calculator")
        private String calculator;

        @JsonProperty("provider_capacity")
        private String providerCapacity;

        @JsonProperty("codigo_csb")
        private String csbCode;

        @JsonProperty("nombre_csb")
        private String csbName;

        @JsonProperty("empresa")
        private String company;

        @JsonProperty("firma_promotor")
        private Boolean signaturePromoterIsRequired;

        @JsonProperty("frecuenciaSueldo")
        private List<CatalogDTO> frequencySalary;

        @JsonProperty("signature_color")
        private String signatureColor;

        @JsonProperty("tipo_de_empleado")
        private List<CatalogDTO> employeeType;

        @JsonProperty("tipo_de_cotizacion")
        private List<CatalogDTO> quotationType;

        @JsonProperty("tipo_de_contrato")
        private List<CatalogDTO> contract_type;

        @JsonProperty("puestos")
        private List<CatalogDTO> positions;

        @JsonProperty("video_task")
        private Boolean videoTaskIsRequired;

        private List<DocumentDTO> documents;

        @JsonProperty("anexos")
        private List<AnnexeDTO>  annexes;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CatalogDTO {

        @JsonProperty("codigo")
        private String code;

        @JsonProperty("descripcion")
        private String description;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocumentDTO implements Serializable {

        private String id;
        private String name;
        private String technicalName;

        @JsonProperty("clasification")
        private String classification;

        @JsonProperty("required")
        private Boolean isRequired;

        @JsonProperty("visible")
        private Boolean isVisible;

        private List<FieldDTO> fields;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldDTO implements Serializable {

        private String id;
        private Integer order;
        private String name;
        private String technicalName;

        @JsonProperty("clasification")
        private String classification;
        private String type;

        @JsonProperty("required")
        private Boolean isRequired;
        private String max;
        private Boolean isSpecial;
        private String convertTo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnnexeDTO implements Serializable {

        private String id;
        private String order;
        private String name;
        private String technicalName;
        @JsonProperty("clasification")
        private String classification;
        private String type;
        private Boolean required;
        private String max;
        private Boolean isSpecial;
        private List<String> typeFile;
        private Boolean isClient;
        private String convertTo;
    }

}
