package com.consubanco.model.entities.agreement.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AttachmentConfigVO {
    private String name;
    private String technicalName;
    private Integer maxFiles;
    private Boolean isOcr;
    private Boolean isRecoverable;
    private String namePreviousDocument;
    private Boolean isRequired;
    private Boolean requestToCustomer;
    private List<String> typeFile;

    public Boolean isRequiredToCustomer() {
        return requestToCustomer && isRequired;
    }
}
