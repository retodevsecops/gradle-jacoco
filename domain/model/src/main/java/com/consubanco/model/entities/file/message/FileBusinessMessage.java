package com.consubanco.model.entities.file.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileBusinessMessage implements IExceptionMessage {

    AGREEMENT_NOT_FOUND("BE_AGREEMENT_0001", "Agreement not found.");

    private final String code;
    private final String message;

}
