package com.consubanco.consumer.adapters.process.dto;

import com.consubanco.model.entities.process.Process;
import lombok.Data;

import java.util.List;

@Data
public class ActiveOfferingResDTO {

    private CustomerData customer;
    private OfferData offer;

    @Data
    public static class CustomerData {
        private String bpId;
    }

    @Data
    public static class OfferData {
        private String id;
        private String requestReferenceCRM;
        private AgreementData agreement;
        private List<CreditData> creditList;
    }

    @Data
    public static class AgreementData {
        private String key;
    }

    @Data
    public static class CreditData {
        private String creditNumber;
    }

    public Process toDomainEntity(String processId) {
        return Process.builder()
                .id(processId)
                .customer(Process.Customer.builder()
                        .bpId(this.customer.bpId)
                        .build())
                .offer(Process.Offer.builder()
                        .id(this.getOffer().getId())
                        .agreementNumber(this.getOffer().getAgreement().getKey())
                        .previousApplicationId(this.getOffer().getRequestReferenceCRM())
                        .loansId(getLoansId())
                        .build())
                .build();
    }

    private List<String> getLoansId() {
        return this.getOffer().getCreditList().stream()
                .map(CreditData::getCreditNumber)
                .toList();
    }

}
