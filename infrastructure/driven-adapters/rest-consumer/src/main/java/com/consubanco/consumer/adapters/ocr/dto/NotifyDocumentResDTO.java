package com.consubanco.consumer.adapters.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NotifyDocumentResDTO {
    private boolean success;
    private String transactionId;
}
