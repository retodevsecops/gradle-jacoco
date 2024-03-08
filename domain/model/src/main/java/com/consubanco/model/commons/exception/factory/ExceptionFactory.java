package com.consubanco.model.commons.exception.factory;

import com.consubanco.model.commons.exception.BusinessException;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.commons.exception.message.IExceptionMessage;
import com.consubanco.model.entities.agreement.Agreement;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class ExceptionFactory {

    public static Mono<? extends Agreement> buildBusiness(IExceptionMessage exceptionMessage) {
        return Mono.error(new BusinessException(exceptionMessage));
    }

    public static Mono<Error> buildTechnical(IExceptionMessage exceptionMessage) {
        return Mono.error(new TechnicalException(exceptionMessage));
    }

    public static Mono<Error> buildTechnical(Throwable cause, IExceptionMessage exceptionMessage) {
        return Mono.error(new TechnicalException(cause, exceptionMessage));
    }

}