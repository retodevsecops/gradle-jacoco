package com.consubanco.postgresql.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class ConnectionConfig extends AbstractR2dbcConfiguration {

    private final ConnectionProperties connectionProperties;

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        PostgresqlConnectionConfiguration connectionConfiguration = connectionConfiguration();
        ConnectionFactory connectionFactory = new PostgresqlConnectionFactory(connectionConfiguration);
        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder(connectionFactory)
                .maxIdleTime(Duration.ofMinutes(connectionProperties.getPool().getMaxIdleTime()))
                .initialSize(connectionProperties.getPool().getInitialSize())
                .maxSize(connectionProperties.getPool().getMaxSize())
                .maxCreateConnectionTime(Duration.ofSeconds(connectionProperties.getPool().getMaxCreateConnectionTime()))
                .maxAcquireTime(Duration.ofSeconds(connectionProperties.getPool().getMaxAcquireTime()))
                .maxLifeTime(Duration.ofHours(connectionProperties.getPool().getMaxLifeTime()))
                .build();
        return new ConnectionPool(poolConfiguration);
    }

    private PostgresqlConnectionConfiguration connectionConfiguration() {
        return PostgresqlConnectionConfiguration.builder()
                        .host(connectionProperties.getHost())
                        .port(connectionProperties.getPort())
                        .database(connectionProperties.getDatabase())
                        .username(connectionProperties.getUsername())
                        .password(connectionProperties.getPassword())
                        .connectTimeout(Duration.ofSeconds(connectionProperties.getConnectionTimeout()))
                        .statementTimeout(Duration.ofSeconds(connectionProperties.getStatementTimeout()))
                        .build();
    }

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    @Bean
    public ResourceDatabasePopulator databasePopulator() {
        return new ResourceDatabasePopulator(new ClassPathResource(connectionProperties.getScript()));
    }

}
