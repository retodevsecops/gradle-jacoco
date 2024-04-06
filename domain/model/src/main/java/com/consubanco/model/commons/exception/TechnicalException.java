package com.consubanco.model.commons.exception;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private final IExceptionMessage exceptionMessage;

    public TechnicalException(Throwable cause, IExceptionMessage exceptionMessage) {
        super(cause);
        this.exceptionMessage = exceptionMessage;
    }

    public TechnicalException(String cause, IExceptionMessage exceptionMessage) {
        super(cause);
        this.exceptionMessage = exceptionMessage;
    }

    public TechnicalException(IExceptionMessage technicalMessage) {
        this.exceptionMessage = technicalMessage;
    }

}