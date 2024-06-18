package com.consubanco.api.exception;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.commons.util.ParamsUtil;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.BusinessException;
import com.consubanco.model.commons.exception.TechnicalException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.consubanco.api.exception.ErrorFactory.*;
import static reactor.core.publisher.Mono.just;


@Order(-2)
@Component
public class ExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final CustomLogger logger;

    public ExceptionHandler(DefaultErrorAttributes errorAttributes, ApplicationContext applicationContext,
                            ServerCodecConfigurer serverCodecConfigurer,
                            final CustomLogger customLogger) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
        this.logger = customLogger;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::buildErrorResponse);
    }

    private Mono<ServerResponse> buildErrorResponse(ServerRequest request) {
        return accessError(request)
                .flatMap(Mono::error)
                .onErrorResume(TechnicalException.class, responseTechnicalError(request))
                .onErrorResume(BusinessException.class, responseBusinessError(request))
                .onErrorResume(ResponseStatusException.class, responseStatusError(request))
                .onErrorResume(responseDefaultError(request))
                .cast(ServerResponse.class);
    }

    private Mono<Throwable> accessError(ServerRequest request) {
        return just(request)
                .map(this::getError)
                .doOnNext(logger::error);
    }

    private Function<TechnicalException, Mono<ServerResponse>> responseTechnicalError(ServerRequest request) {
        return technicalException -> ParamsUtil.getDomain(request)
                .flatMap(domain -> buildFromTechnicalException(technicalException, domain))
                .doOnNext(logger::error)
                .flatMap(HttpResponseUtil::internalError);
    }

    private Function<BusinessException, Mono<ServerResponse>> responseBusinessError(ServerRequest request) {
        return businessException -> ParamsUtil.getDomain(request)
                .flatMap(domain -> buildFromBusinessException(businessException, domain))
                .doOnNext(logger::error)
                .flatMap(HttpResponseUtil::conflict);
    }

    private Function<ResponseStatusException, Mono<ServerResponse>> responseStatusError(ServerRequest request) {
        return statusException -> ParamsUtil.getDomain(request)
                .flatMap(domain -> buildFromResponseStatus(domain).apply(statusException))
                .doOnNext(logger::error)
                .flatMap(error -> HttpResponseUtil.buildResponse(statusException.getStatusCode(), error));
    }

    private Function<Throwable, Mono<ServerResponse>> responseDefaultError(ServerRequest serverRequest) {
        return exception -> ParamsUtil.getDomain(serverRequest)
                .flatMap(domain -> buildResponseDefault(exception.getMessage(), domain))
                .flatMap(HttpResponseUtil::internalError);
    }



}
