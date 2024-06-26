package com.consubanco.consumer.adapters.agreement.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis.promoter")
public class AgreementApisProperties {
    private String channel;
    private String apiGetAgreement;
}
