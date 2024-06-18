package com.consubanco.consumer.adapters.ocr;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis.ocr")
public class OcrApiProperties {
    private String applicationId;
    private String apiNotifyDocument;
    private String apiGetDataDocument;
}
