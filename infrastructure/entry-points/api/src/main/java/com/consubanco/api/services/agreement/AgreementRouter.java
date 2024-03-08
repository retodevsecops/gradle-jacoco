package com.consubanco.api.services.agreement;

import com.consubanco.api.services.agreement.AgreementHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AgreementRouter {

    private static final String AGREEMENT_NUMBER_PATH_PARAM = "/{agreementNumber}";
    @Value("${entry.api.path-services.agreement}")
    private String agreementServicePath;
    @Bean
    public RouterFunction<ServerResponse> routerFunction(AgreementHandler agreementHandler) {
        return route(GET(agreementServicePath.concat(AGREEMENT_NUMBER_PATH_PARAM)), agreementHandler::findByNumber);
    }

}
