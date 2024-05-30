package com.consubanco.usecase.document;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
import com.consubanco.model.entities.document.constant.DocumentNames;
import com.consubanco.model.entities.document.gateway.SignedDocumentGateway;
import com.consubanco.model.entities.document.vo.DocumentSignatureRequestVO;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.process.Process;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;
import reactor.util.function.Tuple3;

import java.time.LocalDateTime;

import static com.consubanco.model.entities.document.message.DocumentBusinessMessage.FAILED_LOAD_DOCUMENT;
import static com.consubanco.model.entities.file.constant.FileConstants.documentsDirectory;
import static com.consubanco.model.entities.loan.message.LoanBusinessMessage.APPLICANT_RECORD_NOT_FOUND;

@RequiredArgsConstructor
public class GenerateNom151UseCase {

    private final SignedDocumentGateway signedDocumentGateway;
    private final FileRepository fileRepository;
    private final AgreementGateway agreementGateway;

    public Mono<File> execute(Process process) {
        return process.checkRequiredData()
                .flatMap(this::getSignedApplicationRecord)
                .filter(file -> file.checkCreationDays(signedDocumentGateway.getValidDays()))
                .switchIfEmpty(signDocumentWithNom151(process));
    }

    private Mono<File> getSignedApplicationRecord(Process process) {
        return Mono.just(process.getOfferId())
                .map(FileConstants::signedApplicantRecordDirectory)
                .flatMap(fileRepository::getByNameWithoutSignedUrl);
    }
    
    private Mono<File> signDocumentWithNom151(Process process) {
        return getData(process)
                .flatMap(TupleUtils.function(this::processNom151));
    }

    private Mono<Tuple3<Agreement, File, Process>> getData(Process process) {
        Mono<Agreement> findAgreement = agreementGateway.findByNumber(process.getAgreementNumber());
        Mono<File> getApplicationRecord = getUnsignedApplicationRecord(process.getOfferId());
        return Mono.zip(findAgreement, getApplicationRecord, Mono.just(process));
    }

    private Mono<File> getUnsignedApplicationRecord(String offerId) {
        return Mono.just(offerId)
                .map(FileConstants::unsignedApplicantRecordDirectory)
                .flatMap(fileRepository::getByNameWithoutSignedUrl)
                .switchIfEmpty(ExceptionFactory.buildBusiness(APPLICANT_RECORD_NOT_FOUND));
    }

    private Mono<File> processNom151(Agreement agreement, File file, Process process) {
        DocumentSignatureRequestVO signatureRequest = buildSignatureRequest(process, file);
        return generateNom151(agreement, signatureRequest)
                .map(nom151 -> buildFile(nom151, process.getOfferId()))
                .flatMap(fileRepository::save);
    }

    private DocumentSignatureRequestVO buildSignatureRequest(Process process, File file) {
        return DocumentSignatureRequestVO.builder()
                .id(process.getOfferId() + LocalDateTime.now())
                .documentInBase64(file.getContent())
                .showSignatures(true)
                .processId(process.getId())
                .build();
    }

    private Mono<String> generateNom151(Agreement agreement, DocumentSignatureRequestVO signatureRequest) {
        if (agreement.isMN()) return generateNom151ForMN(signatureRequest);
        return generateNom151ForCSB(signatureRequest);
    }

    private Mono<String> generateNom151ForCSB(DocumentSignatureRequestVO signatureRequest) {
        return signedDocumentGateway.loadDocumentForCSB(signatureRequest)
                .filter(result -> result)
                .flatMap(status -> signedDocumentGateway.getSignedDocumentForCSB(signatureRequest.getId()))
                .switchIfEmpty(ExceptionFactory.buildBusiness(FAILED_LOAD_DOCUMENT));
    }

    private Mono<String> generateNom151ForMN(DocumentSignatureRequestVO signatureRequest) {
        return signedDocumentGateway.loadDocumentForMN(signatureRequest)
                .filter(result -> result)
                .flatMap(status -> signedDocumentGateway.getNom151ForMN(signatureRequest.getId()))
                .switchIfEmpty(ExceptionFactory.buildBusiness(FAILED_LOAD_DOCUMENT));
    }

    private static File buildFile(String contentInBase64, String offerId) {
        return File.builder()
                .name(DocumentNames.APPLICANT_RECORD)
                .content(contentInBase64)
                .directoryPath(documentsDirectory(offerId))
                .extension(FileExtensions.PDF)
                .build();
    }

}
