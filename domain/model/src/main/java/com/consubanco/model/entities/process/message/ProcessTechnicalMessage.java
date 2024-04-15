package com.consubanco.model.entities.process.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProcessTechnicalMessage implements IExceptionMessage {

    API_OFFER_ERROR("TE_PROCESS_0001", "Error when querying process data.");

    private final String code;
    private final String message;

}
