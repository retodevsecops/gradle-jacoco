package com.consubanco.consumer.commons;

import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@UtilityClass
public class ClientExceptionFactory {

    private static final String FORMAT_REQUEST_ERROR = "Request error to %s:%s with message error: %s";

    public static TechnicalException requestError(WebClientRequestException exception, IExceptionMessage type) {
        String method = exception.getMethod().name();
        String uri = exception.getUri().toString();
        String message = exception.getMessage();
        String cause = String.format(FORMAT_REQUEST_ERROR, method, uri, message);
        return ExceptionFactory.buildTechnical(cause, type);
    }

}
