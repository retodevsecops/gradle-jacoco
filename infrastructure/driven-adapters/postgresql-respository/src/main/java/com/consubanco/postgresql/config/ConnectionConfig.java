package com.consubanco.postgresql.config;

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

@Configuration
@RequiredArgsConstructor
public class ConnectionConfig extends AbstractR2dbcConfiguration {

    private final ConnectionProperties connectionProperties;

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        System.out.println(connectionProperties.toString());
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(connectionProperties.getHost())
                        .port(connectionProperties.getPort())
                        .database(connectionProperties.getDatabase())
                        .username(connectionProperties.getUsername())
                        .password(connectionProperties.getPassword())
                        .build());
    }

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        return initializer;
    }

    @Bean
    public ResourceDatabasePopulator databasePopulator() {
        return new ResourceDatabasePopulator(new ClassPathResource("db/migration"));
    }

}
