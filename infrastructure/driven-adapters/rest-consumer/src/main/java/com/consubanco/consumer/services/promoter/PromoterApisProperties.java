package com.consubanco.consumer.services.promoter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.rest-consumer.apis.api-connect")
public class PromoterApisProperties {
    private String applicationId;
    private String apiSearchInterlocutor;
    private String apiBranchesPromoter;
}
