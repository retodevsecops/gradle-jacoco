package com.consubanco.consumer.adapters.loan.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis.api-connect")
public class LoanApisProperties {
    private String apiCreateApplication;
}