package com.consubanco.consumer.adapters.document.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class GetDocsPreviousApplicationReqDTO {

    @JsonProperty("findDocsPrevAppRequestBO")
    private RequestData data;

    @Data
    @Builder
    public static class RequestData {

        private String applicationId;

        @JsonProperty("folioApplication")
        private String previousApplicationId;
        private List<Document> documents;
    }

    @Data
    public static class Document {
        private String id;
        private String technicalName;
    }

    public GetDocsPreviousApplicationReqDTO(String previousApplicationId) {
        this.data = RequestData.builder()
                .applicationId("")
                .previousApplicationId(previousApplicationId)
                .documents(null)
                .build();
    }

}
