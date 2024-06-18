package com.consubanco.model.entities.otp;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.consubanco.model.entities.otp.message.OtpBusinessMessage.INCORRECT_OTP;
import static com.consubanco.model.entities.otp.message.OtpBusinessMessage.REQUIRED_OTP;
import static com.consubanco.model.entities.otp.message.OtpMessage.DATA_REQUIRED;

@Getter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
public class Otp {

    private static final String REGEX = "\\d{6}";
    private final String code;
    private final String customerBpId;
    private final String latitude;
    private final String longitude;
    private final String ip;
    private final String userAgent;


    public Mono<Otp> checkRequiredData() {
        if (Objects.isNull(code) || code.isBlank()) return ExceptionFactory.monoBusiness(REQUIRED_OTP, DATA_REQUIRED);
        if (Objects.isNull(latitude) || latitude.isBlank())
            return ExceptionFactory.monoBusiness(REQUIRED_OTP, DATA_REQUIRED);
        if (Objects.isNull(longitude) || longitude.isBlank())
            return ExceptionFactory.monoBusiness(REQUIRED_OTP, DATA_REQUIRED);
        if (Objects.isNull(ip) || ip.isBlank()) return ExceptionFactory.monoBusiness(REQUIRED_OTP, DATA_REQUIRED);
        if (Objects.isNull(userAgent) || userAgent.isBlank())
            return ExceptionFactory.monoBusiness(REQUIRED_OTP, DATA_REQUIRED);
        if (!code.matches(REGEX)) return ExceptionFactory.buildBusiness(INCORRECT_OTP);
        return Mono.just(this);
    }

    public Otp addCustomerBp(String customerBpId) {
        return this.toBuilder()
                .customerBpId(customerBpId)
                .build();
    }

}
