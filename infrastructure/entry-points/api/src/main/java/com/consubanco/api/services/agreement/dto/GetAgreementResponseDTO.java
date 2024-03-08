package com.consubanco.api.services.agreement.dto;

import com.consubanco.model.entities.agreement.Agreement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetAgreementResponseDTO {

    private String id;
    private String number;
    private String name;
    private String businessName;

    public GetAgreementResponseDTO(Agreement agreement) {
        this.id = agreement.getId();
        this.number = agreement.getNumber();
        this.name = agreement.getName();
        this.businessName = agreement.getBusinessName();
    }

}
