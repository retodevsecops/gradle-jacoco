package com.consubanco.consumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapters.rest-consumer")
public class HttpClientProperties {
    private int timeout;
}
