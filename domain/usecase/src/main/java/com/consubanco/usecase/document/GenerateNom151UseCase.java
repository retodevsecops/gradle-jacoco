package com.consubanco.usecase.document;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.document.constant.DocumentNames;
import com.consubanco.model.entities.document.gateway.DocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.file.constant.FileConstants.documentsDirectory;
import static com.consubanco.model.entities.loan.message.LoanBusinessMessage.APPLICANT_RECORD_NOT_FOUND;

@RequiredArgsConstructor
public class GenerateNom151UseCase {

    private final DocumentGateway documentGateway;
    private final FileRepository fileRepository;

    public Mono<File> execute(Process process) {
        return process.checkRequiredData()
                .map(Process::getOfferId)
                .flatMap(this::getApplicationRecord)
                .flatMap(file -> generateNom151(file, process.getOfferId()));
    }

    private Mono<File> getApplicationRecord(String offerId) {
        return Mono.just(offerId)
                .map(FileConstants::applicantRecordDirectory)
                .flatMap(fileRepository::getByNameWithoutSignedUrl)
                .switchIfEmpty(ExceptionFactory.buildBusiness(APPLICANT_RECORD_NOT_FOUND));
    }

    private Mono<File> generateNom151(File applicationRecordFile, String offerId) {
        return documentGateway.generateNom151(applicationRecordFile.contentDecode(), applicationRecordFile.getName())
                .map(nom151 -> buildFile(applicationRecordFile, offerId, nom151))
                .flatMap(fileRepository::save);
    }

    private static File buildFile(File applicationRecordFile, String offerId, String nom151) {
        return File.builder()
                .name(DocumentNames.documentNameWithNom151(applicationRecordFile.getName()))
                .content(nom151)
                .directoryPath(documentsDirectory(offerId))
                .extension(FileExtensions.PDF)
                .build();
    }

}
