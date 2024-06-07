package com.consubanco.consumer.adapters.document.dto;

import com.consubanco.model.entities.document.vo.GenerateDocumentVO;
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

    public GenerateDocumentRequestDTO(GenerateDocumentVO generateDocumentVO, Map<String, Object> payload) {
        this.documents = documentsToDTO(generateDocumentVO.getDocuments());
        this.payload = addAttachmentsToPayload(payload, generateDocumentVO);
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

    private Map<String, Object> addAttachmentsToPayload(Map<String, Object> payload, GenerateDocumentVO generateDocumentVO) {
        if (Objects.nonNull(generateDocumentVO.getAttachments()) && !generateDocumentVO.getAttachments().isEmpty()) {
            List<AttachmentDTO> attachments = generateDocumentVO.getAttachments().stream().map(this::getAttachmentDTO).toList();
            payload.put("documents", attachments);
        }
        return payload;
    }

    private Map<String, Object> enableIndividualDocumentsInPayload(Map<String, Object> payload) {
        payload.put("detach", true);
        return payload;
    }

    private AttachmentDTO getAttachmentDTO(GenerateDocumentVO.Attachment attachmentVO) {
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
