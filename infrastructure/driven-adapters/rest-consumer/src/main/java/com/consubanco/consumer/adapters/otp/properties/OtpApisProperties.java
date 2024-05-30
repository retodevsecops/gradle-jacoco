package com.consubanco.consumer.adapters.otp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis.renex")
public class OtpApisProperties {
    private String apiValidateOtp;
}
