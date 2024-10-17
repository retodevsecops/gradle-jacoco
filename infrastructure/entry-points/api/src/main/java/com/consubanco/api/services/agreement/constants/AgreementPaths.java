package com.consubanco.api.services.agreement.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AgreementPaths {
    public static final String AGREEMENT_NUMBER_PATH_PARAM = "/{agreementNumber}";
    public static final String ATTACHMENTS_PATH = "/attachments/{processId}";
    public static final String GENERATE_DOCUMENTS_PATH = "/documents/{processId}/generate";
}
