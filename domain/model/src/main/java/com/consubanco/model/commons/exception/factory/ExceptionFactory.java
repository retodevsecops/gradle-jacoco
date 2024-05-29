package com.consubanco.model.commons.exception.factory;

import com.consubanco.model.commons.exception.BusinessException;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@UtilityClass
public class ExceptionFactory {

    public static <T> Mono<T> monoBusiness(IExceptionMessage exceptionMessage, String detail) {
        return Mono.error(new BusinessException(detail, exceptionMessage));
    }

    public static <T> Mono<T> buildBusiness(IExceptionMessage exceptionMessage) {
        return Mono.error(new BusinessException(exceptionMessage));
    }

    public static BusinessException buildBusiness(String detail, IExceptionMessage exceptionMessage) {
        return new BusinessException(detail, exceptionMessage);
    }

    public static TechnicalException buildTechnical(IExceptionMessage exceptionMessage) {
        return new TechnicalException(exceptionMessage);
    }

    public static TechnicalException buildTechnical(Throwable cause, IExceptionMessage exceptionMessage) {
        return new TechnicalException(cause, exceptionMessage);
    }

    public static TechnicalException buildTechnical(String cause, IExceptionMessage exceptionMessage) {
        return new TechnicalException(cause, exceptionMessage);
    }

    public static Function<Throwable, TechnicalException> throwTechnicalError(IExceptionMessage message) {
        return error -> buildTechnical(error, message);
    }

    public static <T> Mono<T> monoTechnicalError(String cause, IExceptionMessage message){
        return Mono.error(new TechnicalException(cause, message));
    }

}
