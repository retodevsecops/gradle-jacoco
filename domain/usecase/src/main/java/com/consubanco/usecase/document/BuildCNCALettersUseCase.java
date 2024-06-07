package com.consubanco.usecase.document;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.document.constant.DocumentNames;
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
                .flatMap(Process::checkRequiredData)
                .flatMap(Process::checkOfferLoans)
                .flatMap(this::processCNCALetter);
    }

    private Mono<File> processCNCALetter(Process.Offer offer) {
        return getCNCALetter(offer.getId())
                .filter(file -> file.checkCreationDays(documentGateway.validDaysCNCA()))
                .switchIfEmpty(generateUnifiedCNCALetter(offer));
    }

    private Mono<File> getCNCALetter(String offerId) {
        return Mono.just(offerId)
                .map(FileConstants::cncaLetterRoute)
                .flatMap(fileRepository::getByNameWithSignedUrl);
    }

    private Mono<File> generateUnifiedCNCALetter(Process.Offer offer) {
        return getContentsOfCNCALetters(offer.getLoansId())
                .collectList()
                .flatMap(pdfDocumentGateway::merge)
                .map(pdf -> buildFile(offer.getId(), pdf))
                .flatMap(fileRepository::saveWithSignedUrl);
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

    private File buildFile(String offerId, String pdf) {
        return File.builder()
                .name(DocumentNames.CNCA_LETTER)
                .content(pdf)
                .directoryPath(cncaDirectory(offerId))
                .extension(FileExtensions.PDF)
                .build();
    }

}
