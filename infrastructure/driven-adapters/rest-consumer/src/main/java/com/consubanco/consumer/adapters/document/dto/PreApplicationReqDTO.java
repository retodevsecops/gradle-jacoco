package com.consubanco.consumer.adapters.document.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class PreApplicationReqDTO {

    @JsonProperty("getPreApplicationDataRequestBO")
    private DataDTO data;

    @Data
    @Builder
    public static class DataDTO {

        private String applicationId; // quemado

        @JsonProperty("promotorBp")
        private String promoterBp; // es el mismo promotor para todos
        private String customerBp; // lo retorna el servicio de oferta de juanca
        private String referenceCRMApplication; // se obtiene del servicio de juanca
        private Catalog agreement; // lo debe retornar el servicio de oferta de juanca
        private Catalog product; // lo debe retornar el servicio de oferta de juanca
        private String priceGroupId; // lo debe retornar el servicio de oferta de juanca
        private String termDesc; // Se construye con los datos term y frequency retornado por el api de juanca
        private String amount; // Se obtiene del api de juanca
        private String discount; // Se obtiene del api de juanca
        private String cat; // ???
        private String sourceChannel; // Que canal debo enviar ???
        private String paymentCapacity; // ???
    }

    @Data
    public static class Catalog {
        private String key;
        private String description;
    }

    public PreApplicationReqDTO() {
        this.data = DataDTO.builder()
                .build();
    }
}
