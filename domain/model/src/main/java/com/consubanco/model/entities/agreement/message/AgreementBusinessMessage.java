package com.consubanco.model.entities.agreement.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgreementBusinessMessage implements IExceptionMessage {

    AGREEMENT_NOT_FOUND("BE_AGREEMENT_0001", "Agreement not found."),
    AGREEMENT_CONFIG_NOT_FOUND("BE_AGREEMENT_0002", "No agreement configuration was found.");

    private final String code;
    private final String message;

}
