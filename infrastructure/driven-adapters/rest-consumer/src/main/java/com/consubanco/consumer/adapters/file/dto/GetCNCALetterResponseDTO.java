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
        private String url;
        private String base64;
        private DocumentDTO document;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocumentDTO implements Serializable {

        private String id;
        private String name;
        private String technicalName;

        @JsonProperty("clasification")
        private String classification;

        @JsonProperty("required")
        private Boolean isRequired;

        @JsonProperty("visible")
        private Boolean isVisible;
        private List<FieldDTO> fields;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldDTO implements Serializable {

        private String id;
        private String name;
        private String technicalName;

        @JsonProperty("clasification")
        private String classification;
        private String type;

        @JsonProperty("required")
        private Boolean isRequired;
        private String value;
    }

}
