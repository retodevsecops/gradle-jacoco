package com.consubanco.consumer.adapters.file.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public class PayloadDTO {

    private String id;
    @JsonProperty("created_at")
    private String createdAt;

    private DataSeller dataSeller;
    private EmploymentData employmentData;
    private ExceptionProtocol exceptionProtocol;

    @JsonProperty("idDocumentData")
    private DocumentData documentData;

    @JsonProperty("offer")
    private Offer offer;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataSeller implements Serializable {

        @JsonProperty("apellidoMaterno")
        private String LastNameMaternal;

        @JsonProperty("apellidoPaterno")
        private String LastNamePaternal;
        private String bpId;

        @JsonProperty("nombre1")
        private String firstName;

        @JsonProperty("nombre2")
        private String secondName;

        private String rfc;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmploymentData implements Serializable {

        @JsonProperty("")
        private String entryDate;

        @JsonProperty("fechaTrabajo")
        private String dateJob;
        private Boolean lastReceiptIsCurrent;

        @JsonProperty("numeroEmpleado")
        private String numberEmployee;

        @JsonProperty("sueldoMensual")
        private String salaryMonthly;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExceptionProtocol implements Serializable {
        private String dateProtocol;
        private Boolean declined;
        private Boolean exceptionProtocolWasSelected;
        private Boolean extremity;
        private String officialFolio;
        private String officialName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocumentData implements Serializable {
        private String ocr;
        private String type;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Offer implements Serializable {

        private Quoter quoter;

        @JsonProperty("agreemen")
        private Agreement agreement;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Quoter implements Serializable {

        @JsonProperty("CAT")
        private String cat;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Agreement implements Serializable {

        private Branch branch;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Branch implements Serializable {

        @JsonProperty("distribuidor")
        private Distributor distributor;

        @JsonProperty("empresa")
        private Company company;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Distributor implements Serializable {
            @JsonProperty("distributorName")
            private String name;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Company implements Serializable {
            @JsonProperty("enterpriseName")
            private String name;
        }

    }

}
