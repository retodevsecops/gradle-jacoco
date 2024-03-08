package com.consubanco.model.commons.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalMessage implements IExceptionMessage {

    TECHNICAL_SERVER_ERROR("MST0001", "Internal server error");

    private final String code;
    private final String message;

}
