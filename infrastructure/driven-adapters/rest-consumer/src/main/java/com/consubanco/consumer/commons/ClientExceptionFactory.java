package com.consubanco.consumer.commons;

import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.util.Optional;

@UtilityClass
public class ClientExceptionFactory {

    private static final String FORMAT_REQUEST_ERROR = "Request error to %s:%s with message error: %s";
    private static final String FORMAT_RESPONSE_ERROR = "Response error to %s:%s with body: %s";

    public static TechnicalException requestError(WebClientRequestException exception, IExceptionMessage type) {
        String method = exception.getMethod().name();
        String uri = exception.getUri().toString();
        String message = exception.getMessage();
        String cause = String.format(FORMAT_REQUEST_ERROR, method, uri, message);
        return ExceptionFactory.buildTechnical(cause, type);
    }

    public static TechnicalException responseError(WebClientResponseException exception, IExceptionMessage type) {
        String method = getMethod(exception.getRequest());
        String uri = getUri(exception.getRequest());
        String message = exception.getResponseBodyAsString();
        String cause = String.format(FORMAT_RESPONSE_ERROR, method, uri, message);
        return ExceptionFactory.buildTechnical(cause, type);
    }

    private static String getMethod(HttpRequest request) {
        return Optional.ofNullable(request)
                .map(HttpRequest::getMethod)
                .map(HttpMethod::name)
                .orElse("Unknown Method");
    }

    private static String getUri(HttpRequest request) {
        return Optional.ofNullable(request)
                .map(HttpRequest::getURI)
                .map(URI::toString)
                .orElse("Unknown URI");
    }

}
