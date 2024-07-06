package com.consubanco.consumer.adapters.ocr.dto;

import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class GetMetadataResDTO {
    private boolean success;
    private String transactionId;
    private String documentType;
    private DataDTO data;

    @Data
    @NoArgsConstructor
    public static class DataDTO {
        private InnerDataDTO data;
    }

    @Data
    @NoArgsConstructor
    public static class InnerDataDTO {
        private DataExtractionDTO dataExtraction;
    }

    @Data
    @NoArgsConstructor
    public static class DataExtractionDTO {
        private MetadataDTO metadata;
        private String id;
    }

    @Data
    @NoArgsConstructor
    public static class MetadataDTO {
        private MetadataDataDTO data;
        private boolean status;
    }

    @Data
    @NoArgsConstructor
    public static class MetadataDataDTO {

        @JsonProperty("id_status")
        private int idStatus;

        @JsonProperty("json_analytics")
        private JsonAnalyticsDTO jsonAnalytics;
        private String status;
    }

    @Data
    @NoArgsConstructor
    public static class JsonAnalyticsDTO {
        private ExtractDTO extract;
    }

    @Data
    @NoArgsConstructor
    public static class ExtractDTO {
        private List<ExtractionFieldDTO> extractionFields;
    }

    @Data
    @NoArgsConstructor
    public static class ExtractionFieldDTO {
        private String variableName;

        @JsonProperty("Confidence")
        private double confidence;

        @JsonProperty("Value")
        private String value;
    }

    public List<ExtractionFieldDTO> getListOfExtractionFields() {
        return Optional.ofNullable(this.data)
                .map(dataDTO -> dataDTO.data)
                .map(innerDataDTO -> innerDataDTO.dataExtraction)
                .map(dataExtraction -> dataExtraction.metadata)
                .map(metadata -> metadata.data)
                .map(metadataDataDTO -> metadataDataDTO.jsonAnalytics)
                .map(jsonAnalytics -> jsonAnalytics.extract)
                .map(extract -> extract.extractionFields)
                .orElse(Collections.emptyList());
    }

    public List<OcrDataVO> extractionFieldsToModel() {
        return getListOfExtractionFields()
                .stream()
                .map(extractionFieldDTO -> OcrDataVO.builder()
                        .name(extractionFieldDTO.getVariableName())
                        .value(extractionFieldDTO.getValue())
                        .confidence(extractionFieldDTO.getConfidence())
                        .build())
                .toList();
    }

}