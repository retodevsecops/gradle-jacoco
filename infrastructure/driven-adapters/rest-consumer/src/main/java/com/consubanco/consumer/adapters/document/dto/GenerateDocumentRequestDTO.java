package com.consubanco.consumer.adapters.document.dto;

import com.consubanco.model.entities.file.vo.AttachmentVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class GenerateDocumentRequestDTO {

    private List<DocumentDTO> documents;
    private Map<String, Object> payload;

    public GenerateDocumentRequestDTO(List<String> documents, List<AttachmentVO> attachments, Map<String, Object> payload) {
        this.documents = documentsToDTO(documents);
        this.payload = addAttachmentsToPayload(payload, attachments);
    }

    public GenerateDocumentRequestDTO(List<String> documents, Map<String, Object> payload) {
        this.documents = documentsToDTO(documents);
        this.payload = enableIndividualDocumentsInPayload(payload);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocumentDTO implements Serializable {
        private String technicalName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentDTO implements Serializable {
        private String technicalName;
        private List<UrlDTO> urls;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UrlDTO implements Serializable {
        private String url;
    }

    private Map<String, Object> addAttachmentsToPayload(Map<String, Object> payload, List<AttachmentVO> attachmentsList) {
        if (Objects.nonNull(attachmentsList) && !attachmentsList.isEmpty()) {
            List<AttachmentDTO> attachments = attachmentsList.stream().map(this::getAttachmentDTO).toList();
            payload.put("documents", attachments);
        }
        return payload;
    }

    private Map<String, Object> enableIndividualDocumentsInPayload(Map<String, Object> payload) {
        payload.put("detach", true);
        return payload;
    }

    private AttachmentDTO getAttachmentDTO(AttachmentVO attachmentVO) {
        List<UrlDTO> urlsListDTO = attachmentVO.getUrls().stream().map(UrlDTO::new).toList();
        return new AttachmentDTO(attachmentVO.getName(), urlsListDTO);
    }


    private List<DocumentDTO> documentsToDTO(List<String> documents) {
        return documents.stream()
                .map(document -> DocumentDTO.builder()
                        .technicalName(document)
                        .build())
                .collect(Collectors.toList());
    }

}
