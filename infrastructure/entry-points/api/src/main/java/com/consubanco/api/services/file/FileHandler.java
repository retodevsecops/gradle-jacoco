package com.consubanco.api.services.file;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.usecase.agreement.AgreementUseCase;
import com.consubanco.usecase.file.FileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FileHandler {

    private static final String ACCOUNT_NUMBER_PATH_PARAM = "accountNumber";
    private final FileUseCase fileUseCase;

    public Mono<ServerResponse> getCNCALetter(ServerRequest serverRequest) {
        String accountNumber = serverRequest.pathVariable(ACCOUNT_NUMBER_PATH_PARAM);
        return fileUseCase.getCNCALetterByAccountNumber(accountNumber)
                .flatMap(HttpResponseUtil::Ok);
    }

}
