package com.consubanco.model.entities.agreement.message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AgreementMessage {

    private static final String AGREEMENT_CONFIG_NOT_FOUNT = "No agreement settings were found for the number %s";

    public String configNotFound(String agreementNumber) {
        return String.format(AGREEMENT_CONFIG_NOT_FOUNT, agreementNumber);
    }

}
