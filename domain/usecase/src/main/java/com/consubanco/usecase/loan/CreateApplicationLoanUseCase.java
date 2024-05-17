package com.consubanco.usecase.loan;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.loan.LoanApplication;
import com.consubanco.model.entities.loan.gateway.LoanApplicationRepository;
import com.consubanco.model.entities.loan.gateway.LoanGateway;
import com.consubanco.model.entities.otp.Otp;
import com.consubanco.model.entities.otp.gateway.OtpGateway;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.Map;

import static com.consubanco.model.entities.otp.message.OtpBusinessMessage.INVALID_OTP;

@RequiredArgsConstructor
public class CreateApplicationLoanUseCase {

    private final static String MESSAGE = "The loan application has been successfully completed.";
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final FileRepository fileRepository;
    private final PayloadDocumentGateway payloadDocGateway;
    private final LoanGateway loanGateway;
    private final LoanApplicationRepository loanApplicationRepository;
    private final OtpGateway otpGateway;

    public Mono<Map<String, String>> execute(String processId, String otp) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(process -> checkOtp(new Otp(otp, process.getCustomer().getBpId())).thenReturn(process))
                .flatMap(process -> Mono.zip(getCreateApplicationTemplate(), payloadDocGateway.getAllData(processId))
                        .flatMap(TupleUtils.function(loanGateway::buildApplicationData))
                        .flatMap(applicationData -> createApplication(process, otp, applicationData)))
                .thenReturn(Map.of("message", MESSAGE));
    }

    private Mono<Void> createApplication(Process process, String otp, Map<String, Object> applicationData) {
        return loanGateway.createApplication(applicationData)
                .map(resultCreateApplication -> new LoanApplication(process.getId(), otp, applicationData, resultCreateApplication))
                .flatMap(loanApplicationRepository::saveApplication);
    }

    private Mono<String> finishOffer(Process process) {
        return Mono.just("");
    }

    private Mono<String> sendMail() {
        return Mono.just("");
    }

    private Mono<String> getCreateApplicationTemplate() {
        return fileRepository.getCreateApplicationTemplateWithoutSignedUrl()
                .map(File::getContent);
    }

    private Mono<Void> checkOtp(Otp otp) {
        return otp.checkRequiredData()
                .flatMap(otpGateway::checkOtp)
                .filter(otpValid -> otpValid)
                .switchIfEmpty(ExceptionFactory.buildBusiness(INVALID_OTP))
                .then();
    }

}
