package com.consubanco.model.entities.otp.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OtpTechnicalMessage implements IExceptionMessage {

    API_VALIDATE_OTP_ERROR("TE_LOAN_0001", "Error when consuming the validate otp api.");

    private final String code;
    private final String message;

}
