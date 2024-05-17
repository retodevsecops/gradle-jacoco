package com.consubanco.consumer.adapters.otp.dto;

import lombok.Data;

@Data
public class ValidateOTPResDTO {
    private String otp;
    private Boolean isValid;
}
