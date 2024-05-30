package com.consubanco.model.entities.otp.gateway;

import com.consubanco.model.entities.otp.Otp;
import reactor.core.publisher.Mono;

public interface OtpGateway {
    Mono<Boolean> checkOtp(Otp otp);
}
