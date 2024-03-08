package com.consubanco.model.commons.exception;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final IExceptionMessage exceptionMessage;

    public BusinessException(IExceptionMessage exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}