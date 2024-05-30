package com.consubanco.consumer.adapters.document.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateNom151ReqDTO {

    @JsonProperty("cdoDigitalSignatureRequestBO")
    private RequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestData implements Serializable {
        private String applicationId;
        private String documentHash;
        private String documentName;
    }

    public GenerateNom151ReqDTO(String applicationId, String documentHash, String documentName) {
        this.data = new RequestData(applicationId, documentHash, documentName);
    }
}
