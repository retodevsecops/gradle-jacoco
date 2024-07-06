package com.consubanco.consumer.adapters.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class GetMetadataReqDTO {
    private String applicationId;
    private String transactionId;
}
