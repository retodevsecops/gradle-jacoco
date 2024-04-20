package com.consubanco.model.entities.agreement.vo;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import lombok.*;

import java.util.List;
import java.util.Objects;

import static com.consubanco.model.entities.agreement.message.AgreementBusinessMessage.DOCUMENTS_COMPOUND_NOT_CONFIG;
import static com.consubanco.model.entities.agreement.message.AgreementBusinessMessage.DOCUMENTS_VISIBLE_NOT_CONFIG;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgreementConfigVO {

    private String agreementNumber;
    private List<CompoundDocument> compoundDocuments;
    private List<String> customerVisibleDocuments;

    @Data
    public static class CompoundDocument {
        private String name;
        private List<String> documents;
    }

    public AgreementConfigVO checkCompoundDocuments() {
        if (Objects.isNull(compoundDocuments) || compoundDocuments.isEmpty()){
            ExceptionFactory.buildBusiness(DOCUMENTS_COMPOUND_NOT_CONFIG);
        }
        return this;
    }

    public AgreementConfigVO checkCustomerVisibleDocuments() {
        if (Objects.isNull(compoundDocuments) || compoundDocuments.isEmpty()){
            ExceptionFactory.buildBusiness(DOCUMENTS_VISIBLE_NOT_CONFIG);
        }
        return this;
    }

}
