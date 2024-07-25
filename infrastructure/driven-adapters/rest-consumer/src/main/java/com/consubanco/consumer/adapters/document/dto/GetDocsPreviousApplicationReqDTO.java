package com.consubanco.consumer.adapters.document.dto;

import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class GetDocsPreviousApplicationReqDTO {

    private static final String DOCUMENT_ID = "1";

    @JsonProperty("findDocsPrevAppRequestBO")
    private RequestData data;

    @Data
    @Builder
    public static class RequestData {

        private String applicationId;

        @JsonProperty("folioApplication")
        private String previousApplicationId;
        private List<DocumentData> documents;
    }

    @Data
    @AllArgsConstructor
    public static class DocumentData {
        private String id;
        private String technicalName;
    }

    public GetDocsPreviousApplicationReqDTO(String applicationId, String previousApplicationId, List<AttachmentConfigVO> docs) {
        this.data = RequestData.builder()
                .applicationId(applicationId)
                .previousApplicationId(previousApplicationId)
                .documents(docs.stream()
                        .map(doc -> new DocumentData(DOCUMENT_ID, doc.getNamePreviousDocument()))
                        .toList())
                .build();
    }

}