package com.consubanco.model.entities.process;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Process {

    private String id;
    private String agreementId;
    private Offer offer;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Offer {
        private String id;
        private List<String> loansId;

    }

    public Process(String id, String agreementId, String offerId, List<String> loansId) {
        this.id = id;
        this.agreementId = agreementId;
        this.offer = new Offer(offerId, loansId);
    }
}
