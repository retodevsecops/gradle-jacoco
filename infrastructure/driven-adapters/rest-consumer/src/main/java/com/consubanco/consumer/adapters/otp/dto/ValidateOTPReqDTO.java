package com.consubanco.consumer.adapters.otp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateOTPReqDTO {
    private String otp;
    private String bpId;
}
