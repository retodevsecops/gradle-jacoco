package com.consubanco.consumer.adapters.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateCNCALetterReqDTO {
    private String applicationId;
    private String accountId;
}
