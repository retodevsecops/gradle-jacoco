package com.consubanco.consumer.adapters.file.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis.api-connect.get-cnca-letter")
public class GetCNCALetterApiProperties {
        private String endpoint;
        private String applicationId;
}
