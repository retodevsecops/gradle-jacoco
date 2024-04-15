package com.consubanco.api.services.file.dto;

import com.consubanco.model.entities.file.vo.AttachmentVO;
import com.consubanco.model.entities.file.vo.FileDataVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetAndUploadDocumentReqDTO {

    @Schema(description = "The name under which the file will be saved.", requiredMode = REQUIRED)
    private String fileName;

    @Schema(description = "Documents to be contained in the resulting unified pdf document.", example = "[\"formato-unico-caratula\", \"domiciliacion\"]", requiredMode = REQUIRED)
    private List<String> documents;

    @Schema(description = "Attachments documents to included at the end of pdf document.", requiredMode = NOT_REQUIRED)
    private List<AttachmentDTO> attachments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class AttachmentDTO {

        @Schema(description = "Technical name of the attached file.", example = "recibo-nomina", requiredMode = REQUIRED)
        private String name;

        @Schema(description = "Links where the attached files are.", example = "[\"https://storage.googleapis.com/recibo-nomina.jpg\"]", requiredMode = REQUIRED)
        private List<String> urls;
    }

    public List<AttachmentVO> attachmentsFromDTO() {
        if (attachments == null) return Collections.emptyList();
        return this.attachments.stream()
                .map(attachmentDTO -> AttachmentVO.builder()
                        .name(attachmentDTO.getName())
                        .urls(attachmentDTO.getUrls())
                        .build())
                .toList();
    }

    public FileDataVO getData(){
        return FileDataVO.builder()
                .documents(this.getDocuments())
                .attachments(this.attachmentsFromDTO())
                .build();
    }

}
