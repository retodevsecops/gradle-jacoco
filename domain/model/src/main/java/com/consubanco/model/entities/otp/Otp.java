package com.consubanco.model.entities.otp;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.consubanco.model.entities.otp.message.OtpBusinessMessage.INCORRECT_OTP;
import static com.consubanco.model.entities.otp.message.OtpBusinessMessage.REQUIRED_OTP;
import static com.consubanco.model.entities.otp.message.OtpMessage.DATA_REQUIRED;

@Data
@AllArgsConstructor
public class Otp {

    private static final String REGEX = "\\d{6}";
    private String code;
    private String customerBpId;


    public Mono<Otp> checkRequiredData(){
        if (Objects.isNull(code) || code.isBlank()) return ExceptionFactory.monoBusiness(REQUIRED_OTP, DATA_REQUIRED);
        if(!code.matches(REGEX)) return ExceptionFactory.buildBusiness(INCORRECT_OTP);
        return Mono.just(this);
    }

}
