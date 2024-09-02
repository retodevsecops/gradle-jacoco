package com.consubanco.consumer.services.nom151;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis.nom151")
public class Nom151ApiProperties {

    private Integer validTimeMin;
    private String endpoint;
    private Credential credentials;
    private Action actions;
    private RetryStrategy retryStrategy;

    @Data
    public static class Credential {
        private CredentialData csb;
        private CredentialData masNomina;
    }

    @Data
    public static class CredentialData {
        private String user;
        private String password;
    }

    @Data
    public static class Action {
        private String loadDocument;
        private String getDocumentSigned;
        private String getNom151;
    }

    @Data
    public static class RetryStrategy {
        private Integer maxRetries;
        private Integer retryDelay;
    }

    public String getUserCSB() {
        return this.credentials.csb.user;
    }

    public String getPasswordCSB() {
        return this.credentials.csb.password;
    }

    public String getUserMN() {
        return this.credentials.masNomina.user;
    }

    public String getPasswordMN() {
        return this.credentials.masNomina.password;
    }


    public Duration retryDelay() {
        return Duration.ofSeconds(this.retryStrategy.retryDelay);
    }

}
