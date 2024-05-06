package com.consubanco.consumer.adapters.document.dto;

import com.consubanco.model.entities.document.vo.PreviousDocumentVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetDocsPreviousApplicationResDTO {

    private static final String RESPONSE = "findDocsPrevAppResponseBO";

    @JsonProperty(RESPONSE)
    private ResponseData data;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseData {
        private String status;
        private String code;
        private String response;
        private List<DocumentData> documents;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentData {
        private List<FilesData> files;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilesData {
        private String fileName;
        private String base64;
    }

    public Flux<PreviousDocumentVO> toDomainEntity() {
        if (this.getData() == null || this.getData().getDocuments() == null) return Flux.empty();
        return Flux.fromIterable(this.getData().getDocuments())
                .map(doc -> doc.getFiles().stream().findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(file -> PreviousDocumentVO.builder()
                        .name(getFilenameWithoutExtension(file.getFileName()))
                        .content(file.getBase64())
                        .extension(getFileExtension(file.getFileName()))
                        .build());
    }

    private String getFilenameWithoutExtension(String filename) {
        return filename.substring(0, filename.lastIndexOf('.'));
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    public static Integer getResponseCode(Map<String, Object> response) {
        Map<String, Object> resBO = (Map<String, Object>) response.get(RESPONSE);
        return Integer.parseInt((String) resBO.get("code"));
    }

    public static String getResponseMessage(Map<String, Object> response) {
        Map<String, Object> resBO = (Map<String, Object>) response.get(RESPONSE);
        return (String) resBO.get("response");
    }

}
