package com.consubanco.usecase.loan.helpers;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
import com.consubanco.model.entities.document.constant.DocumentNames;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.vo.FileStorageVO;
import com.consubanco.model.entities.otp.Otp;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.otp.CheckOtpUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.List;

import static com.consubanco.model.entities.loan.message.LoanBusinessMessage.MISSING_DOCUMENTS;

@RequiredArgsConstructor
public class LoanApplicationValidationHelper {

    private final AgreementGateway agreementGateway;
    private final AgreementConfigRepository agreementConfigRepository;
    private final FileRepository fileRepository;
    private final CheckOtpUseCase checkOtpUseCase;

    public Mono<Process> execute(Process process, Otp otp) {
        return Mono.zip(this.verifyOtp(process, otp), this.verifyDocuments(process))
                .thenReturn(process);
    }

    private Mono<Process> verifyOtp(Process process, Otp otp) {
        return Mono.just(process.getCustomer().getBpId())
                .map(otp::addCustomerBp)
                .flatMap(checkOtpUseCase::execute)
                .thenReturn(process);
    }

    private Mono<Process> verifyDocuments(Process process) {
        Mono<List<String>> documentsRequiredForAgreement = documentsRequiredForAgreement(process.getAgreementNumber());
        Mono<List<String>> getOfferFilesFromStorage = getOfferFilesFromStorage(process.getOfferId());
        return Mono.zip(documentsRequiredForAgreement, getOfferFilesFromStorage)
                .flatMap(TupleUtils.function(this::checkExistenceDocuments))
                .thenReturn(process);
    }

    private Mono<List<String>> documentsRequiredForAgreement(String agreementNumber) {
        return Mono.zip(requiredDocumentsByAgreement(agreementNumber), requiredAttachmentsByAgreement(agreementNumber))
                .map(tuple -> {
                    tuple.getT1().addAll(tuple.getT2());
                    tuple.getT1().add(DocumentNames.CNCA_LETTER);
                    tuple.getT1().add(DocumentNames.UNSIGNED_APPLICANT_RECORD);
                    return tuple.getT1();
                });
    }

    private Mono<List<String>> requiredDocumentsByAgreement(String agreementNumber) {
        return agreementGateway.findByNumber(agreementNumber)
                .map(Agreement::getDocuments)
                .flatMapMany(Flux::fromIterable)
                .map(Agreement.Document::getFields)
                .flatMap(Flux::fromIterable)
                .filter(Agreement.Document.Field::getIsRequired)
                .map(Agreement.Document.Field::getNameFromTechnicalName)
                .distinct()
                .collectList();
    }

    private Mono<List<String>> requiredAttachmentsByAgreement(String agreementNumber) {
        return agreementConfigRepository.getConfigByAgreement(agreementNumber)
                .map(AgreementConfigVO::getAttachmentsDocuments)
                .flatMapMany(Flux::fromIterable)
                .filter(AttachmentConfigVO::getIsRequired)
                .map(AttachmentConfigVO::getTechnicalName)
                .distinct()
                .collectList();
    }

    private Mono<List<String>> getOfferFilesFromStorage(String offerId) {
        return Mono.just(offerId)
                .map(FileConstants::offerDirectory)
                .flatMapMany(fileRepository::listByFolder)
                .map(FileStorageVO::getName)
                .collectList();
    }

    private Mono<Void> checkExistenceDocuments(List<String> documentsRequired, List<String> documentsInStorage) {
        return Flux.fromIterable(documentsRequired)
                .filter(documentName -> !documentsInStorage.contains(documentName))
                .collectList()
                .filter(list -> !list.isEmpty())
                .flatMap(list -> ExceptionFactory.monoBusiness(MISSING_DOCUMENTS, String.join(", ", list)));
    }

}
