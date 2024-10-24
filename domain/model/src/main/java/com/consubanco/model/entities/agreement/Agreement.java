package com.consubanco.model.entities.agreement;

import com.consubanco.model.entities.agreement.constant.CompanyNames;
import lombok.*;

import java.util.Comparator;
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
    private List<Catalog> contractType;
    private List<Catalog> positions;
    private Boolean videoTaskIsRequired;
    private List<Document> documents;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Catalog {
        private String code;
        private String description;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Document {
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
        private List<Field> fields;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder(toBuilder = true)
        public static class Field {

            private final static String REGEX = "/";
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

            public String getNameFromTechnicalName() {
                if (technicalName != null && technicalName.contains(REGEX)) {
                    int index = technicalName.lastIndexOf(REGEX);
                    return technicalName.substring(index + 1);
                }
                return technicalName;
            }

        }

    }

    public boolean isMN() {
        return CompanyNames.MN.equalsIgnoreCase(this.company);
    }

    public List<String> getDocumentsToGenerate() {
        return this.getDocuments().parallelStream()
                .flatMap(document -> document.getFields().stream())
                .map(Agreement.Document.Field::getTechnicalName)
                .distinct()
                .map(docName -> isMN() ? docName: CompanyNames.CSB.toLowerCase().concat("/").concat(docName))
                .toList();
    }

    public List<String> getSortedDocuments() {
        return this.getDocuments().parallelStream()
                .flatMap(document -> document.getFields().stream())
                .sorted(Comparator.comparing(Document.Field::getOrder))
                .map(Document.Field::getNameFromTechnicalName)
                .distinct()
                .toList();
    }

}
