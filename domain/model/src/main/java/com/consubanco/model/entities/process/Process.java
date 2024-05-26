package com.consubanco.model.entities.process;

import lombok.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildBusiness;
import static com.consubanco.model.entities.process.message.ProcessBusinessMessage.INCOMPLETE_DATA;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Process {

    private String id;
    private Customer customer;
    private Offer offer;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Customer {
        private String bpId;
    }

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

    public String getPreviousApplicationId() {
        return this.getOffer().getPreviousApplicationId();
    }

    public Mono<Process> checkRequiredData(){
        if (Objects.isNull(id) || Objects.isNull(customer) || Objects.isNull(offer)) return buildBusiness(INCOMPLETE_DATA);
        return Mono.just(this);
    }

    public String getOfferId() {
        return this.offer.id;
    }

}
