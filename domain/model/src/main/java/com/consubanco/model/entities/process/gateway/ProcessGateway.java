package com.consubanco.model.entities.process.gateway;

import com.consubanco.model.entities.process.Process;
import reactor.core.publisher.Mono;

public interface ProcessGateway {
    Mono<Process> getProcessById(String id);
}
