package com.consubanco.consumer.config;

import com.consubanco.consumer.config.filters.WebClientLoggingFilter;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class RestConsumerConfig {

    private static final String CLIENT_ID_HEADER = "X-IBM-Client-Id";
    private static final String AUTH_BEARER_VALUE = "Bearer %s";
    private final String authTokenApiPromoter;
    private final String clientIdApiConnect;
    private final HttpClientProperties clientProperties;
    private final WebClientLoggingFilter webClientLoggingFilter;

    public RestConsumerConfig(final @Value("${adapter.rest-consumer.apis.promoter.auth-token}") String token,
                              final @Value("${adapter.rest-consumer.apis.api-connect.client-id}") String clientId,
                              final HttpClientProperties clientProperties,
                              final WebClientLoggingFilter webClientLoggingFilter) {
        this.authTokenApiPromoter = token;
        this.clientIdApiConnect = clientId;
        this.clientProperties = clientProperties;
        this.webClientLoggingFilter = webClientLoggingFilter;
    }

    @Bean
    public ModelMapper buildModelMapper() {
        return new ModelMapper();
    }

    @Bean("ApiPromoterClient")
    public WebClient buildWebClient(WebClient.Builder builder) {
        return builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, String.format(AUTH_BEARER_VALUE, authTokenApiPromoter))
                .clientConnector(getClientHttpConnector())
                .filter(webClientLoggingFilter)
                .build();
    }

    @Bean("ApiConnectClient")
    public WebClient buildWebClientApiConnect(WebClient.Builder builder) {
        return builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(CLIENT_ID_HEADER, clientIdApiConnect)
                .clientConnector(getClientHttpConnector())
                .filter(webClientLoggingFilter)
                .build();
    }

    @Bean("clientGetFiles")
    public WebClient buildClientGetFiles(WebClient.Builder builder) {
        return builder
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(config -> config.defaultCodecs().maxInMemorySize(1024 * 1024 * 10))
                        .build())
                .clientConnector(getClientHttpConnector())
                .build();
    }

    @SneakyThrows
    private SslContext getBuildSslContext() {
        return SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
    }

    private ClientHttpConnector getClientHttpConnector() {
        int timeout = clientProperties.getTimeout();
        return new ReactorClientHttpConnector(HttpClient.create()
                .compress(true)
                .keepAlive(true)
                .option(CONNECT_TIMEOUT_MILLIS, timeout)
                .secure(sslContextSpec -> sslContextSpec.sslContext(getBuildSslContext()))
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                }));
    }

}
