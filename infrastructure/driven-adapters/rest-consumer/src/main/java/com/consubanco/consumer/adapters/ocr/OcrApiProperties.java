package com.consubanco.consumer.adapters.ocr;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis.ocr")
public class OcrApiProperties {

    private Double confidence;
    private String applicationId;
    private String apiNotifyDocument;
    private String apiGetDataDocument;
    private Integer initialDelayTime;
    private Integer maxRetries;
    private Integer retryDelay;
    private Integer maxRetryDelay;
    private Integer daysRangeForPayStubsValidation;

    public Duration initialDelayInSeconds() {
        return Duration.ofSeconds(initialDelayTime);
    }

    public Duration retryDelayInSeconds() {
        return Duration.ofSeconds(retryDelay);
    }

    public Duration maxRetryDelayInMinutes() {
        return Duration.ofMinutes(maxRetryDelay);
    }

}
