package com.consubanco.usecase.file;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateways.FileGateway;
import com.consubanco.model.entities.file.gateways.FileRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RequiredArgsConstructor
public class FileUseCase {

    private final static String FILE_NAME_CNCA_LETTER = "carta-de-liquidacion_%s";
    private final FileGateway fileGateway;
    private final FileRepository fileRepository;

    public Flux<File> buildCNCALetters(String offerId, List<String> loansId) {
        return getAllCNCALetter(offerId, loansId)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(fileRepository::save)
                .sequential();

    }

    private Flux<File> getAllCNCALetter(String offerId, List<String> loansId) {
        return Flux.fromIterable(loansId)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(loanId -> getCNCALetter(offerId, loanId))
                .sequential();
    }
    private Mono<File> getCNCALetter(String offerId, String loanId) {
        return fileGateway.getContentCNCALetter(loanId)
                .map(contentCNCALetter -> File.builder()
                        .name(String.format(FILE_NAME_CNCA_LETTER, loanId))
                        .content(contentCNCALetter)
                        .bucketName(offerId)
                        .build());
    }

}
