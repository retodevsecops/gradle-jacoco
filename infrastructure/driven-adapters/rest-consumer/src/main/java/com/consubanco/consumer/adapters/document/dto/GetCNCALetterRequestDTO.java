package com.consubanco.consumer.adapters.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCNCALetterRequestDTO {

    private CncaLetterRequestBO cncaLetterRequestBO;

    public GetCNCALetterRequestDTO(String applicationId, String accountId) {
        this.cncaLetterRequestBO = new CncaLetterRequestBO(applicationId, accountId);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CncaLetterRequestBO implements Serializable {
        private String applicationId;
        private String accountId;
    }

}
