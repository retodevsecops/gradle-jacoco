package com.consubanco.consumer.config.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RestConsumerTechnicalMessage implements IExceptionMessage {

    CONVERSION_ERROR("TE_CONVERSION_ERROR", "Error converting object to string.");

    private final String code;
    private final String message;

}
