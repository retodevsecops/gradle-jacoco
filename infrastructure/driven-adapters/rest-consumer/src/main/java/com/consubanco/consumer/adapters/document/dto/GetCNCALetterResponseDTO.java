package com.consubanco.consumer.adapters.document.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.monoTechnicalError;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.CNCA_LETTER_ERROR;

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

    public Mono<String> getFileAsBase64() {
        return Mono.justOrEmpty(this.getData().getFiles())
                .filter(files -> !files.isEmpty())
                .map(files -> files.get(0).getBase64())
                .switchIfEmpty(monoTechnicalError(data.getResponse(), CNCA_LETTER_ERROR));
    }

}
