package com.consubanco.model.entities.file.gateways;

import com.consubanco.model.entities.file.File;
import reactor.core.publisher.Mono;

public interface FileRepository {
    Mono<File> getCNCALetter(String accountNumber);
}
