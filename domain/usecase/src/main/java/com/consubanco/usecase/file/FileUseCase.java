package com.consubanco.usecase.file;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateways.FileRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FileUseCase {

    private final FileRepository fileRepository;

    public Mono<File> getCNCALetterByAccountNumber(String accountNumber) {
        return this.fileRepository.getCNCALetter(accountNumber);
    }

}
