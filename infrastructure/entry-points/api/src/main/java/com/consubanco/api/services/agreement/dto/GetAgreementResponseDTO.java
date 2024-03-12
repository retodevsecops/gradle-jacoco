package com.consubanco.api.services.agreement.dto;

import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.document.Document;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetAgreementResponseDTO {

    @Schema(description = "Agreement identifier")
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
    private List<Agreement.Catalog> frequencySalary;
    private String signatureColor;
    private List<Agreement.Catalog> employeeType;
    private List<Agreement.Catalog> quotationType;
    private List<Agreement.Catalog> contract_type;
    private List<Agreement.Catalog> positions;
    private Boolean videoTaskIsRequired;
    private List<Document> documents;
    private List<Agreement.Annexe>  annexes;

}
