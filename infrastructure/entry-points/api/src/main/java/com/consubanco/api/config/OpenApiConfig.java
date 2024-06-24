package com.consubanco.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final OpenApiConfigProperties openApiConfigProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(openApiConfigProperties.getTitle())
                        .version(openApiConfigProperties.getVersion())
                        .description(openApiConfigProperties.getDescription()));
    }

}
