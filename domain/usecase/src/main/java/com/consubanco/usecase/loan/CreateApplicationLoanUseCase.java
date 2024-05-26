package com.consubanco.usecase.loan;

import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.loan.LoanApplication;
import com.consubanco.model.entities.loan.gateway.LoanApplicationRepository;
import com.consubanco.model.entities.loan.gateway.LoanGateway;
import com.consubanco.model.entities.otp.Otp;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.otp.CheckOtpUseCase;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.Map;

@RequiredArgsConstructor
public class CreateApplicationLoanUseCase {

    private final static String MESSAGE = "The loan application has been successfully completed.";
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final FileRepository fileRepository;
    private final PayloadDocumentGateway payloadDocGateway;
    private final LoanGateway loanGateway;
    private final LoanApplicationRepository loanApplicationRepository;
    private final CheckOtpUseCase checkOtpUseCase;
    private final BuildDataForApplicationUseCase buildDataForApplicationUseCase;

    public Mono<Map<String, String>> execute(String processId, String otp) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(process -> verifyOtp(process, otp))
                .flatMap(process ->
                        Mono.zip(getCreateApplicationTemplate(), buildDataForApplicationUseCase.execute(process))
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
