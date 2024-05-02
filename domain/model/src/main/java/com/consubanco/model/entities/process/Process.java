package com.consubanco.model.entities.process;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Process {

    private String id;
    private Offer offer;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Offer {
        private String id;
        private String agreementNumber;
        private String previousApplicationId;
        private List<String> loansId;
    }

    public String getAgreementNumber() {
        return this.getOffer().getAgreementNumber();
    }

}
