package com.consubanco.caffeine;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "helpers.caffeine")
public class CaffeineProperties {
    private int expireAfterWrite;
    private int maxSizeElements;
}
