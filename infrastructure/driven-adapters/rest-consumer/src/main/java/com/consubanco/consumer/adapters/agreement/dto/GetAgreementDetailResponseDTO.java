package com.consubanco.consumer.adapters.agreement.dto;

import com.consubanco.model.entities.agreement.Agreement;
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
    private detailDTO detail;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class detailDTO implements Serializable {

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

        @JsonProperty("signature_color")
        private String signatureColor;

        private  List<DocumentDTO> documents;

        @JsonProperty("anexos")
        private List<AnnexeDTO>  annexes;

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
        private Boolean required;
        private Boolean visible;
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
        private Boolean required;
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
