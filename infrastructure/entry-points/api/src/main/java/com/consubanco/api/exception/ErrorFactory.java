package com.consubanco.api.exception;

import com.consubanco.model.commons.exception.BusinessException;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.commons.exception.message.IExceptionMessage;
import com.consubanco.model.commons.exception.message.TechnicalMessage;
import lombok.experimental.UtilityClass;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static reactor.core.publisher.Mono.just;

@UtilityClass
public class ErrorFactory {

    public static Mono<ErrorDTO> errorFromException(Throwable throwable, String pathService) {
        return Mono.error(throwable)
                .onErrorResume(TechnicalException.class, error -> buildFromTechnicalException(error, pathService))
                .onErrorResume(ResponseStatusException.class, buildFromResponseStatus(pathService))
                .onErrorResume(error -> buildResponseDefault(error.getMessage(), pathService))
                .cast(ErrorDTO.class);
    }

    public static Mono<ErrorDTO> buildFromTechnicalException(TechnicalException exception, String domain) {
        ErrorDTO error = buildError(exception.getExceptionMessage(), exception.getMessage(), domain);
        return just(error);
    }

    public static Mono<ErrorDTO> buildFromBusinessException(BusinessException exception, String domain) {
        ErrorDTO error = buildError(exception.getExceptionMessage(), exception.getMessage(), domain);
        return just(error);
    }

    public static Mono<ErrorDTO> buildResponseDefault(String reason, String domain) {
        return Mono.just(buildError(TechnicalMessage.TECHNICAL_SERVER_ERROR, reason, domain));
    }

    public static Function<ResponseStatusException, Mono<ErrorDTO>> buildFromResponseStatus(String pathService) {
        return responseStatusException -> getReason(responseStatusException)
                .flatMap(reason -> buildResponseDefault(reason, pathService));
    }

    private static Mono<String> getReason(ResponseStatusException responseException) {
        return just(responseException.getStatusCode().value())
                .map(String::valueOf)
                .map(status -> String.join(" ", status, responseException.getMessage()));
    }

    public static ErrorDTO buildError(IExceptionMessage exceptionMessage, String reason, String domain) {
        return ErrorDTO.builder()
                .reason(reason)
                .domain(domain)
                .code(exceptionMessage.getCode())
                .message(exceptionMessage.getMessage())
                .build();
    }

}
