package com.consubanco.usecase.loan;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.document.constant.DocumentNames;
import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.loan.LoanApplication;
import com.consubanco.model.entities.loan.gateway.LoanApplicationRepository;
import com.consubanco.model.entities.loan.gateway.LoanGateway;
import com.consubanco.model.entities.otp.Otp;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.CheckOtpUseCase;
import com.consubanco.usecase.document.GenerateNom151UseCase;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.Map;

import static com.consubanco.model.entities.loan.message.LoanBusinessMessage.APPLICANT_RECORD_NOT_FOUND;

@RequiredArgsConstructor
public class CreateApplicationLoanUseCase {

    private final static String MESSAGE = "The loan application has been successfully completed.";
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final FileRepository fileRepository;
    private final PayloadDocumentGateway payloadDocGateway;
    private final LoanGateway loanGateway;
    private final LoanApplicationRepository loanApplicationRepository;
    private final CheckOtpUseCase checkOtpUseCase;
    private final GenerateNom151UseCase generateNom151UseCase;

    public Mono<Map<String, String>> execute(String processId, String otp) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(process -> verifyOtp(process, otp))
                .flatMap(process ->
                        generateNom151(process)
                                .flatMap(file -> Mono.zip(getCreateApplicationTemplate(), payloadDocGateway.getAllData(processId)))
                                .flatMap(TupleUtils.function(loanGateway::buildApplicationData))
                                .flatMap(applicationData -> createApplication(process, otp, applicationData)))
                .thenReturn(Map.of("message", MESSAGE));
    }

    private Mono<Process> verifyOtp(Process process, String otp) {
        return Mono.just(process.getCustomer().getBpId())
                .map(customerBpId -> new Otp(otp, customerBpId))
                .flatMap(checkOtpUseCase::execute)
                .thenReturn(process);
    }

    private Mono<File> generateNom151(Process process) {
        return fileRepository.listByFolderWithoutUrls(FileConstants.offerDirectory(process.getOffer().getId()))
                .filter(file -> file.getName().equalsIgnoreCase(DocumentNames.APPLICANT_RECORD))
                .next()
                .flatMap(file -> generateNom151UseCase.execute(file, process))
                .switchIfEmpty(ExceptionFactory.buildBusiness(APPLICANT_RECORD_NOT_FOUND));
    }

    private Mono<Void> createApplication(Process process, String otp, Map<String, Object> applicationData) {
        return loanGateway.createApplication(applicationData)
                .map(resultCreateApplication -> new LoanApplication(process.getId(), otp, applicationData, resultCreateApplication))
                .flatMap(loanApplicationRepository::saveApplication);
    }

    private Mono<String> getCreateApplicationTemplate() {
        return fileRepository.getCreateApplicationTemplateWithoutSignedUrl()
                .map(File::getContent);
    }

    private Mono<String> finishOffer(Process process) {
        return Mono.just("");
    }

    private Mono<String> sendMail() {
        return Mono.just("");
    }

}
