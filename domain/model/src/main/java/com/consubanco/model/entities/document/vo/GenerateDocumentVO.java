package com.consubanco.model.entities.document.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateDocumentVO {

    private List<String> documents;
    private List<Attachment> attachments;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attachment {
        private String name;
        private List<String> urls;
    }

}
