package com.consubanco.consumer.adapters.document.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GenerateCNCALetterResDTO {

    private String status;
    private String code;
    private String response;
    private Data data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data implements Serializable {

        private String code;

        @JsonProperty("Message")
        private String message;

        @JsonProperty("FileName")
        private String fileName;

        @JsonProperty("Base64")
        private String base64;

    }

    public Boolean checkCNCAIfExists() {
        return Objects.nonNull(this.data) && Objects.nonNull(this.data.base64) && !this.data.base64.isBlank();
    }

}
