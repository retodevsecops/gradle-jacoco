package com.consubanco.consumer.adapters.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@AllArgsConstructor
public class GetMetadataResDTO {
    private boolean success;
    private String transactionId;
    private String documentType;
    private Map<String, Object> data;
}
