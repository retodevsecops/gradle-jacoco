package com.consubanco.model.entities.agreement;

import com.consubanco.model.entities.document.Document;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Agreement {

    private String id;
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
    private List<Document>  annexes;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Catalog {
        private String code;
        private String description;
    }

}
