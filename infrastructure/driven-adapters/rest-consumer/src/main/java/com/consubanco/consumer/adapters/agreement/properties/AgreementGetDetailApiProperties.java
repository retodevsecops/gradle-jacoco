package com.consubanco.consumer.adapters.agreement.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis.promoter.agreement-get-detail")
public class AgreementGetDetailApiProperties {
        private String endpoint;
        private String channel;
}
