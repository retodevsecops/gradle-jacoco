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
    private Integer connectionTimeout;
    private Integer statementTimeout;
    private PoolConnectionData pool;

    @Data
    public static class PoolConnectionData {
        private Integer initialSize;
        private Integer maxSize;
        private Integer maxIdleTime;
        private Integer maxCreateConnectionTime;
        private Integer maxAcquireTime;
        private Integer maxLifeTime;
    }

}
