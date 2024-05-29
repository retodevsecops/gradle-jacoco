package com.consubanco.usecase.otp;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.otp.Otp;
import com.consubanco.model.entities.otp.gateway.OtpGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.otp.message.OtpBusinessMessage.INVALID_OTP;

@RequiredArgsConstructor
public class CheckOtpUseCase {

    private final OtpGateway otpGateway;

    public Mono<Void> execute(Otp otp) {
        return otp.checkRequiredData()
                .flatMap(otpGateway::checkOtp)
                .filter(otpValid -> otpValid)
                .switchIfEmpty(ExceptionFactory.buildBusiness(INVALID_OTP))
                .then();
    }

}
