package com.consubanco.usecase.file;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileGateway;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.consubanco.model.entities.file.constant.FileConstants.cncaDirectory;
import static com.consubanco.model.entities.file.constant.FileConstants.cncaFormatName;

@RequiredArgsConstructor
public class BuildCNCALettersUseCase {

    private final FileGateway fileGateway;
    private final FileRepository fileRepository;
    private final GetProcessByIdUseCase getProcessByIdUseCase;

    public Flux<File> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .map(Process::getOffer)
                .flatMapMany(offer -> getAllCNCALetter(offer.getId(), offer.getLoansId()))
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(fileRepository::save)
                .sequential();

    }

    private Flux<File> getAllCNCALetter(String offerId, List<String> loansId) {
        return Flux.fromIterable(loansId)
                .distinct()
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(loanId -> getCNCALetter(offerId, loanId))
                .sequential();
    }

    private Mono<File> getCNCALetter(String offerId, String loanId) {
        return fileGateway.getContentCNCALetter(loanId)
                .map(contentCNCALetter -> File.builder()
                        .name(cncaFormatName(loanId))
                        .content(contentCNCALetter)
                        .directoryPath(cncaDirectory(offerId))
                        .extension(FileExtensions.PDF)
                        .build());
    }

}
