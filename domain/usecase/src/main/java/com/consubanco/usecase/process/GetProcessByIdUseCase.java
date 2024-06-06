package com.consubanco.usecase.process;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.process.Process;
import com.consubanco.model.entities.process.gateway.ProcessGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.process.message.ProcessBusinessMessage.ID_PROCESS_REQUIRED;
import static com.consubanco.model.entities.process.message.ProcessBusinessMessage.PROCESS_NOT_FOUND;

@RequiredArgsConstructor
public class GetProcessByIdUseCase {

    private final ProcessGateway processGateway;

    public Mono<Process> execute(String id) {
        return this.checkProcessId(id)
                .flatMap(processGateway::getProcessById)
                .switchIfEmpty(ExceptionFactory.buildBusiness(PROCESS_NOT_FOUND));
    }

    private Mono<String> checkProcessId(String id) {
        return Mono.justOrEmpty(id)
                .switchIfEmpty(ExceptionFactory.buildBusiness(ID_PROCESS_REQUIRED));
    }

}
