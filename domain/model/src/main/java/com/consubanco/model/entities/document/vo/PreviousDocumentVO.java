package com.consubanco.model.entities.document.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PreviousDocumentVO {
    private String name;
    private String content;
    private String extension;
}
