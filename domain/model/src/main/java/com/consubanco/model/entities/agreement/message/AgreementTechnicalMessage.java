package com.consubanco.model.entities.agreement.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgreementTechnicalMessage implements IExceptionMessage {

    API_ERROR("TE_AGREEMENT_0001", "Error when consulting the promoter API to get details agreement."),
    FAIL_GET_CONFIG_STORAGE("TE_AGREEMENT_0002", "Error getting agreements configuration from storage."),
    FAIL_GET_CONFIG_LOCAL("TE_AGREEMENT_0003", "Error getting agreements configuration from local."),
    STRUCTURE_INVALID("TE_AGREEMENT_0004", "The structure of the configuration file is not valid."),
    FAIL_UPLOAD_CONFIG("TE_AGREEMENT_0005", "Error when uploading the agreement configuration file.");


    private final String code;
    private final String message;

}
