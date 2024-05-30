package com.consubanco.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "entry.api.open-api")
public class OpenApiConfigProperties {
    private String title;
    private String version;
    private String description;
}
