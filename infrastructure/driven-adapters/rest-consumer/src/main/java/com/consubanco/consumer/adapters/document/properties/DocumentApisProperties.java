package com.consubanco.consumer.adapters.document.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis")
public class DocumentApisProperties {

    private Promoter promoter;
    private ApiConnect apiConnect;

    @Data
    public static class Promoter {
        private String channel;
        private String apiGenerateDocument;
    }

    @Data
    public static class ApiConnect {
        private Integer validDaysCnca;
        private String applicationId;
        private String apiGetCnca;
        private String searchInterlocutor;
        private String apiDocsPrevious;
    }

    public String getApplicationId() {
        return this.getApiConnect().getApplicationId();
    }

    public String getCNCAApiEndpoint() {
        return this.getApiConnect().getApiGetCnca();
    }

    public String generateDocumentApiEndpoint() {
        return this.getPromoter().getApiGenerateDocument();
    }

}
