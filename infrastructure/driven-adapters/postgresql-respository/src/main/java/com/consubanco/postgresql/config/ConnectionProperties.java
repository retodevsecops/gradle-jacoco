package com.consubanco.postgresql.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.postgresql-repository.connection")
public class ConnectionProperties {
    private String host;
    private Integer port;
    private String database;
    private String username;
    private String password;
    private String script;

}
