package com.consubanco.consumer.config;

import com.consubanco.logger.CustomLogger;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.net.ssl.SSLException;

import java.util.Map;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class RestConsumerConfig {

    private static final String CLIENT_ID_HEADER = "X-IBM-Client-Id";
    private static final String AUTH_BEARER_VALUE = "Bearer %s";
    private final String authTokenApiPromoter;
    private final String clientIdApiConnect;
    private final CustomLogger logger;
    private final HttpClientProperties clientProperties;

    public RestConsumerConfig(final @Value("${adapter.rest-consumer.apis.promoter.auth-token}") String token,
                              final @Value("${adapter.rest-consumer.apis.api-connect.client-id}") String clientId,
                              final CustomLogger logger,
                              final HttpClientProperties clientProperties) {
        this.authTokenApiPromoter = token;
        this.clientIdApiConnect = clientId;
        this.logger = logger;
        this.clientProperties = clientProperties;
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
                .filter(new WebClientLoggingFilter(logger))
                .build();
    }

    @Bean("ApiConnectClient")
    public WebClient buildWebClientApiConnect(WebClient.Builder builder) {
        return builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(CLIENT_ID_HEADER, clientIdApiConnect)
                .clientConnector(getClientHttpConnector())
                .filter(new WebClientLoggingFilter(logger))
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
