package com.consubanco.model.entities.agreement.vo;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import lombok.*;

import java.util.List;
import java.util.Objects;

import static com.consubanco.model.entities.agreement.message.AgreementBusinessMessage.DOCUMENTS_VISIBLE_NOT_CONFIG;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgreementConfigVO {

    private String agreementName;
    private String agreementNumber;
    private String promoterId;
    private String branchId;
    private List<CompoundDocument> compoundDocuments;
    private List<String> customerVisibleDocuments;
    private List<AttachmentConfigVO> attachmentsDocuments;

    @Data
    public static class CompoundDocument {
        private String name;
        private List<DocumentData> documents;
    }

    @Data
    public static class DocumentData {
        private String name;
        private Integer page;
    }

    public boolean checkCompoundDocuments() {
        return !(Objects.isNull(compoundDocuments) || compoundDocuments.isEmpty());
    }

    public AgreementConfigVO checkCustomerVisibleDocuments() {
        if (Objects.isNull(customerVisibleDocuments) || customerVisibleDocuments.isEmpty()) {
            throw ExceptionFactory.business(DOCUMENTS_VISIBLE_NOT_CONFIG);
        }
        return this;
    }

    public List<AttachmentConfigVO> attachments() {
        return attachmentsDocuments;
    }

    public List<String> getOcrAttachmentsTechnicalNames() {
        return this.getAttachmentsDocuments().stream()
                .filter(AttachmentConfigVO::getIsOcr)
                .map(AttachmentConfigVO::getTechnicalName)
                .toList();
    }

}
