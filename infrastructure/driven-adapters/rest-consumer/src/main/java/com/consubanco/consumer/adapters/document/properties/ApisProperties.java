package com.consubanco.consumer.adapters.document.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis")
public class ApisProperties {

    private Renex renex;
    private ApiConnect apiConnect;

    @Data
    public static class ApiConnect {
        private String applicationId;
        private String apiSearchInterlocutor;
    }

    @Data
    public static class Renex {
        private String apiHealthCustomer;
        private String apiCustomerProcess;
        private String apiActiveOffer;
        private String apiHealthOffer;
        private String apiAcceptOffer;
    }

}
