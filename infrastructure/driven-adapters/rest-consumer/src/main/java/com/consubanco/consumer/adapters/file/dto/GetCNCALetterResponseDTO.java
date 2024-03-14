package com.consubanco.consumer.adapters.file.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetCNCALetterResponseDTO {

    @JsonProperty("cncaLetterResponseBO")
    private CncaLetterResponseBO data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CncaLetterResponseBO implements Serializable {
        private String status;
        private String code;
        private String response;
        private List<FileResponseDTO> files;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileResponseDTO implements Serializable {
        private String fileName;
        private String base64;
    }

}
