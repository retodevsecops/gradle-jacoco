package com.consubanco.consumer.adapters.otp;

import com.consubanco.consumer.adapters.otp.dto.ValidateOTPReqDTO;
import com.consubanco.consumer.adapters.otp.dto.ValidateOTPResDTO;
import com.consubanco.consumer.adapters.otp.properties.OtpApisProperties;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.otp.Otp;
import com.consubanco.model.entities.otp.gateway.OtpGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static com.consubanco.consumer.commons.ClientExceptionFactory.requestError;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.otp.message.OtpTechnicalMessage.API_REQUEST_ERROR;
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
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_VALIDATE_OTP_ERROR))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(API_VALIDATE_OTP_ERROR));
    }

}
