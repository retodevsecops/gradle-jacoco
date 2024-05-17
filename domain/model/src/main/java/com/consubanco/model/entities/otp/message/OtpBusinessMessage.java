package com.consubanco.model.entities.otp.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OtpBusinessMessage implements IExceptionMessage {

    REQUIRED_OTP("BE_OTP_0001", "The otp is required."),
    INVALID_OTP("BE_OTP_0002", "The otp is invalid."),
    INCORRECT_OTP("BE_OTP_0003", "Your otp is made up, please don't do that.");

    private final String code;
    private final String message;

}
