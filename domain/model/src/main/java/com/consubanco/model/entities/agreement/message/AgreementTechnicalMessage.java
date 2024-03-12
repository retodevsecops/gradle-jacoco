package com.consubanco.model.entities.agreement.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgreementTechnicalMessage implements IExceptionMessage {

    API_ERROR("TE_AGREEMENT_0001", "Error when consulting the promoter API to get details agreement.");

    private final String code;
    private final String message;

}
