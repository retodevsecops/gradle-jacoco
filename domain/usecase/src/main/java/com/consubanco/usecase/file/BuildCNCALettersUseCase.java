package com.consubanco.usecase.file;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.document.gateway.DocumentGateway;
import com.consubanco.model.entities.document.gateway.PDFDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.consubanco.model.entities.document.message.DocumentBusinessMessage.CNCA_NOT_GENERATED;
import static com.consubanco.model.entities.file.constant.FileConstants.cncaDirectory;

@RequiredArgsConstructor
public class BuildCNCALettersUseCase {

    private final DocumentGateway documentGateway;
    private final FileRepository fileRepository;
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final PDFDocumentGateway pdfDocumentGateway;

    public Mono<File> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .map(Process::getOffer)
                .flatMap(this::generateUnifiedCNCALetter)
                .flatMap(fileRepository::saveWithSignedUrl);

    }

    private Mono<File> generateUnifiedCNCALetter(Process.Offer offer) {
        return getContentsOfCNCALetters(offer.getLoansId())
                .collectList()
                .flatMap(pdfDocumentGateway::merge)
                .map(pdf -> File.builder()
                        .name(FileConstants.FILE_NAME_CNCA_LETTER)
                        .content(pdf)
                        .directoryPath(cncaDirectory(offer.getId()))
                        .extension(FileExtensions.PDF)
                        .build());
    }

    private Flux<String> getContentsOfCNCALetters(List<String> loansId) {
        return Flux.fromIterable(loansId)
                .distinct()
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::getContentCNCALetter)
                .sequential();
    }

    private Mono<String> getContentCNCALetter(String loanId) {
        return documentGateway.generateContentCNCALetter(loanId)
                .switchIfEmpty(ExceptionFactory.buildBusiness(CNCA_NOT_GENERATED));
    }

}
