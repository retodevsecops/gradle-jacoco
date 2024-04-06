package com.consubanco.consumer.adapters.file.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetCNCALetterResponseDTO {

    @JsonProperty("cncaLetterResponseBO")
    private Data data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data implements Serializable {
        private String status;
        private String code;
        private String response;
        private List<FileResponseDTO> files;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileResponseDTO implements Serializable {
        private String fileName;
        private String base64;
    }

}
