package com.consubanco.usecase.loan.usecase;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.loan.LoanApplication;
import com.consubanco.model.entities.loan.gateway.LoanApplicationRepository;
import com.consubanco.model.entities.loan.gateway.LoanGateway;
import com.consubanco.model.entities.loan.vo.ApplicationResponseVO;
import com.consubanco.model.entities.otp.Otp;
import com.consubanco.model.entities.process.Process;
import com.consubanco.model.entities.process.gateway.ProcessGateway;
import com.consubanco.usecase.loan.helpers.ValidateLoanFilesHelper;
import com.consubanco.usecase.otp.CheckOtpUseCase;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.function.TupleUtils;

import java.util.Map;

import static com.consubanco.model.entities.loan.message.LoanBusinessMessage.API_CREATE_APPLICATION_RESPONSE_ERROR;

@RequiredArgsConstructor
public class CreateApplicationLoanUseCase {

    private final static String MESSAGE = "The loan application has been successfully completed.";
    private final CheckOtpUseCase checkOtpUseCase;
    private final ValidateLoanFilesHelper validateLoanFilesHelper;
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final FileRepository fileRepository;
    private final LoanGateway loanGateway;
    private final LoanApplicationRepository loanRepository;
    private final BuildDataForApplicationUseCase buildDataForApplicationUseCase;
    private final ProcessGateway processGateway;

    public Mono<Map<String, String>> execute(String processId, Otp otp) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(process -> executeValidations(process, otp))
                .flatMap(process -> processLoanApplication(otp, process))
                .thenReturn(Map.of("message", MESSAGE));
    }

    private Mono<Process> executeValidations(Process process, Otp otp) {
        return Mono.zip(validateLoanFilesHelper.execute(process), verifyOtp(process, otp))
                .thenReturn(process);
    }

    private Mono<Process> verifyOtp(Process process, Otp otp) {
        return Mono.just(process.getCustomer().getBpId())
                .map(otp::addCustomerBp)
                .flatMap(checkOtpUseCase::execute)
                .thenReturn(process);
    }

    private Mono<Void> processLoanApplication(Otp otp, Process process) {
        return Mono.zip(getCreateApplicationTemplate(), buildDataForApplicationUseCase.execute(process))
                .flatMap(TupleUtils.function(loanGateway::buildApplicationData))
                .flatMap(applicationData -> createApplication(process, otp, applicationData))
                .flatMap(loanApplication -> checkApplication(process, loanApplication));
    }

    private Mono<Void> checkApplication(Process process, LoanApplication loanApplication) {
        if (loanApplication.applicationIsSuccessful()) return finishProcess(process, loanApplication);
        return ExceptionFactory.monoBusiness(API_CREATE_APPLICATION_RESPONSE_ERROR, loanApplication.getResponse().toString());
    }

    private Mono<LoanApplication> createApplication(Process process, Otp otp, Map<String, Object> applicationData) {
        return loanGateway.createApplication(applicationData)
                .map(applicationResponse -> buildLoanApplication(process, otp, applicationData, applicationResponse))
                .flatMap(loanRepository::saveApplication);
    }

    private LoanApplication buildLoanApplication(Process process,
                                                 Otp otp,
                                                 Map<String, Object> applicationData,
                                                 ApplicationResponseVO applicationResponse) {
        return LoanApplication.builder()
                .processId(process.getId())
                .otp(otp.getCode())
                .applicationStatus(applicationResponse.getApplicationStatus())
                .request(applicationData)
                .response(applicationResponse.getApplicationResponse())
                .build();
    }

    private Mono<String> getCreateApplicationTemplate() {
        return fileRepository.getCreateApplicationTemplateWithoutSignedUrl()
                .map(File::getContent);
    }

    private Mono<Void> finishProcess(Process process, LoanApplication loanApplication) {
        Mono.zip(finishOffer(process.getId()), sendMail(process.getId()))
                .flatMap(tuple -> loanRepository.updateOfferAndEmailStatus(loanApplication.getId(), tuple.getT1(), tuple.getT2()))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
        return Mono.empty();
    }

    private Mono<String> finishOffer(String processId) {
        return processGateway.finish(processId);
    }

    private Mono<String> sendMail(String processId) {
        return Mono.just("SENT");
    }

}
