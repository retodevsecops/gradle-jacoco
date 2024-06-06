package com.consubanco.consumer.services.promoter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class BranchesByPromoterReqDTO {

    @JsonProperty("branchesByPromotorRequestBO")
    private DataDTO dataDTO;

    public BranchesByPromoterReqDTO(String applicationId, String promoterBpId) {
        this.dataDTO = new DataDTO(applicationId, promoterBpId);
    }

    @Data
    @AllArgsConstructor
    public static class DataDTO {
        private String applicationId;
        @JsonProperty("promotorBP")
        private String promoterBpId;
    }

}
