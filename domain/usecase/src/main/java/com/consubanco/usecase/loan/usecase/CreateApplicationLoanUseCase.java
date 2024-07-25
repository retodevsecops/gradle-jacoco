package com.consubanco.usecase.loan.usecase;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.loan.LoanApplication;
import com.consubanco.model.entities.loan.gateway.LoanApplicationRepository;
import com.consubanco.model.entities.loan.gateway.LoanGateway;
import com.consubanco.model.entities.loan.vo.ApplicationResponseVO;
import com.consubanco.model.entities.otp.Otp;
import com.consubanco.model.entities.process.Process;
import com.consubanco.model.entities.process.gateway.ProcessGateway;
import com.consubanco.usecase.loan.helpers.LoanApplicationValidationHelper;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.Map;

import static com.consubanco.model.entities.loan.message.LoanBusinessMessage.API_CREATE_APPLICATION_RESPONSE_ERROR;
import static com.consubanco.model.entities.loan.message.LoanBusinessMessage.APPLICANT_RECORD_SIGNED_NOT_FOUND;

@RequiredArgsConstructor
public class CreateApplicationLoanUseCase {

    private static final String MESSAGE = "The loan application has been successfully completed.";
    private final LoanApplicationValidationHelper loanApplicationValidationHelper;
    private final GetProcessByIdUseCase getProcessByIdUseCase;
    private final FileRepository fileRepository;
    private final LoanGateway loanGateway;
    private final LoanApplicationRepository loanRepository;
    private final BuildDataForApplicationUseCase buildDataForApplicationUseCase;
    private final ProcessGateway processGateway;

    public Mono<Map<String, String>> execute(String processId, Otp otp) {
        return getProcessByIdUseCase.execute(processId)
                .flatMap(process -> loanApplicationValidationHelper.execute(process, otp))
                .flatMap(process -> processLoanApplication(otp, process))
                .thenReturn(Map.of("message", MESSAGE));
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

    private Mono<Void> finishProcess(Process process, LoanApplication req) {
        return Mono.zip(finishOffer(process.getId()), sendMail(process))
                .flatMap(tuple -> loanRepository.updateOfferAndEmailStatus(req.getId(), tuple.getT1(), tuple.getT2()));
    }

    private Mono<String> finishOffer(String processId) {
        return processGateway.finish(processId);
    }

    private Mono<String> sendMail(Process process) {
        return getSignedRecordAsBase64(process.getOfferId())
                .flatMap(signedRecordAsBase64 -> loanGateway.sendMail(process, signedRecordAsBase64))
                .map(Enum::name);
    }

    private Mono<String> getSignedRecordAsBase64(String offerId) {
        String route = FileConstants.signedApplicantRecordRoute(offerId);
        return fileRepository.getByNameWithoutSignedUrl(route)
                .map(File::getContent)
                .switchIfEmpty(ExceptionFactory.monoBusiness(APPLICANT_RECORD_SIGNED_NOT_FOUND, route));
    }

}
