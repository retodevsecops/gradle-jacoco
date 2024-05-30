package com.consubanco.model.entities.document.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class DocumentSignatureRequestVO {
    private final String id;
    private final String documentInBase64;
    private final Boolean showSignatures;
    private final String processId;
}
