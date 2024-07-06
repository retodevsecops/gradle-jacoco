package com.consubanco.consumer.adapters.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class NotifyDocumentReqDTO {
    private String applicationId;
    private String gsURI;
    private String documentType;
}
