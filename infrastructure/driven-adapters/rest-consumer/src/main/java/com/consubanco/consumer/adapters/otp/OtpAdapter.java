package com.consubanco.consumer.adapters.otp;

import com.consubanco.consumer.adapters.otp.dto.ValidateOTPReqDTO;
import com.consubanco.consumer.adapters.otp.dto.ValidateOTPResDTO;
import com.consubanco.consumer.adapters.otp.properties.OtpApisProperties;
import com.consubanco.model.entities.otp.Otp;
import com.consubanco.model.entities.otp.gateway.OtpGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.otp.message.OtpTechnicalMessage.API_VALIDATE_OTP_ERROR;

@Service
public class OtpAdapter implements OtpGateway {

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String IP = "ip";
    public static final String USER_AGENT = "User-Agent";
    private final WebClient renexClient;
    private final OtpApisProperties apisProperties;


    public OtpAdapter(final @Qualifier("ApiRenexClient") WebClient renexClient,
                      final OtpApisProperties apisProperties) {
        this.apisProperties = apisProperties;
        this.renexClient = renexClient;
    }

    @Override
    public Mono<Boolean> checkOtp(Otp otp) {
        return renexClient.post()
                .uri(apisProperties.getApiValidateOtp())
                .header(LATITUDE, otp.getLatitude())
                .header(LONGITUDE, otp.getLongitude())
                .header(IP, otp.getIp())
                .header(USER_AGENT, otp.getUserAgent())
                .bodyValue(new ValidateOTPReqDTO(otp.getCode(), otp.getCustomerBpId()))
                .retrieve()
                .bodyToMono(ValidateOTPResDTO.class)
                .map(ValidateOTPResDTO::getIsValid)
                .onErrorMap(throwTechnicalError(API_VALIDATE_OTP_ERROR));
    }

}
