package com.consubanco.consumer.adapters.document.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis")
public class PayloadApisProperties {
    private Renex renex;
    private ApiConnect apiConnect;

    @Data
    public static class ApiConnect {
        private String applicationId;
        private String apiSearchInterlocutor;
    }

    @Data
    public static class Renex {
        private String preApplication;
    }

}
