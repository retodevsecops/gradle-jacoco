package com.consubanco.usecase.document.usecase;

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
import reactor.util.function.Tuple2;
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
                .filter(file -> file.checkCreationMinutes(signedDocumentGateway.getValidMinutes()))
                .switchIfEmpty(signDocumentWithNom151(process));
    }

    private Mono<File> getSignedApplicationRecord(Process process) {
        return Mono.just(process.getOfferId())
                .map(FileConstants::signedApplicantRecordRoute)
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
                .map(FileConstants::unsignedApplicantRecordRoute)
                .flatMap(fileRepository::getByNameWithoutSignedUrl)
                .switchIfEmpty(ExceptionFactory.buildBusiness(APPLICANT_RECORD_NOT_FOUND));
    }

    private Mono<File> processNom151(Agreement agreement, File file, Process process) {
        DocumentSignatureRequestVO signatureRequest = buildSignatureRequest(process, file);
        if (Boolean.TRUE.equals(agreement.isMN()))
            return generateNom151ForMN(signatureRequest, process);
        return generateNom151ForCSB(signatureRequest, process);
    }

    private DocumentSignatureRequestVO buildSignatureRequest(Process process, File file) {
        return DocumentSignatureRequestVO.builder()
                .id(process.getOfferId() + LocalDateTime.now())
                .documentInBase64(file.getContent())
                .showSignatures(true)
                .processId(process.getId())
                .build();
    }

    private Mono<File> generateNom151ForMN(DocumentSignatureRequestVO signatureRequest, Process process) {
        return signedDocumentGateway.loadDocumentForMN(signatureRequest)
                .filter(result -> result)
                .flatMap(status -> Mono.zip(
                        uploadSignedDocumentForMN(signatureRequest, process),
                        uploadNom151ForMN(signatureRequest, process)))
                .map(Tuple2::getT1)
                .switchIfEmpty(ExceptionFactory.buildBusiness(FAILED_LOAD_DOCUMENT));
    }

    private Mono<File> uploadSignedDocumentForMN(DocumentSignatureRequestVO signatureRequest, Process process) {
        return signedDocumentGateway.getSignedDocumentForMN(signatureRequest.getId())
                .map(base64 -> buildApplicantRecordFile(base64, process.getOfferId()))
                .flatMap(fileRepository::save);
    }

    private Mono<File> uploadNom151ForMN(DocumentSignatureRequestVO signatureRequest, Process process) {
        return signedDocumentGateway.getNom151ForMN(signatureRequest.getId())
                .map(base64 -> buildNom151File(process, base64))
                .flatMap(fileRepository::save);
    }

    private Mono<File> generateNom151ForCSB(DocumentSignatureRequestVO signatureRequest, Process process) {
        return signedDocumentGateway.loadDocumentForCSB(signatureRequest)
                .filter(result -> result)
                .flatMap(status -> Mono.zip(
                        uploadSignedDocumentForCSB(signatureRequest, process),
                        uploadNom151ForCSB(signatureRequest, process)))
                .map(Tuple2::getT1)
                .switchIfEmpty(ExceptionFactory.buildBusiness(FAILED_LOAD_DOCUMENT));
    }

    private Mono<File> uploadSignedDocumentForCSB(DocumentSignatureRequestVO signatureRequest, Process process) {
        return signedDocumentGateway.getSignedDocumentForCSB(signatureRequest.getId())
                .map(base64 -> buildApplicantRecordFile(base64, process.getOfferId()))
                .flatMap(fileRepository::save);
    }

    private Mono<File> uploadNom151ForCSB(DocumentSignatureRequestVO signatureRequest, Process process) {
        return signedDocumentGateway.getNom151ForCSB(signatureRequest.getId())
                .map(base64 -> buildNom151File(process, base64))
                .flatMap(fileRepository::save);
    }

    private File buildApplicantRecordFile(String contentInBase64, String offerId) {
        return File.builder()
                .name(DocumentNames.APPLICANT_RECORD)
                .content(contentInBase64)
                .directoryPath(documentsDirectory(offerId))
                .extension(FileExtensions.PDF)
                .build();
    }

    private File buildNom151File(Process process, String base64) {
        return File.builder()
                .name(process.getOfferId())
                .content(base64)
                .directoryPath(documentsDirectory(process.getOfferId()))
                .extension(FileExtensions.CONS)
                .build();
    }

}
