package com.consubanco.model.entities.agreement.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgreementConfigVO {

    private String agreementNumber;
    private List<CompoundDocument> compoundDocuments;

    @Data
    public static class CompoundDocument {
        private String name;
        private List<String> documents;
    }

}
