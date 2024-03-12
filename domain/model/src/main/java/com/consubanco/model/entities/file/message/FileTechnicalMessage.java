package com.consubanco.model.entities.file.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileTechnicalMessage implements IExceptionMessage {

    API_ERROR("TE_FILE_0001", "Error consuming getCNCALetter capability of OPC Service API.");

    private final String code;
    private final String message;

}
