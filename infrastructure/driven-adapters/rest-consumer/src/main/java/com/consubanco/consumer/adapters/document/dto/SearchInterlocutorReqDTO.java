package com.consubanco.consumer.adapters.document.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SearchInterlocutorReqDTO {

    @JsonProperty("searchInterlocutorReqBO")
    private DataDTO dataDTO;

    public SearchInterlocutorReqDTO(String applicationId, String bpId) {
        this.dataDTO = new DataDTO(applicationId, bpId);
    }

    @Data
    @AllArgsConstructor
    public static class DataDTO {
        private String applicationId;
        private String bpId;
    }

}
