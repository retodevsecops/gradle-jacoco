package com.consubanco.consumer.config;

import com.consubanco.consumer.config.filters.AuthTokenRenexFilter;
import com.consubanco.consumer.config.filters.WebClientLoggingFilter;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
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

import java.time.Duration;

import static com.consubanco.consumer.commons.Constants.AUTH_BEARER_VALUE;
import static com.consubanco.consumer.commons.Constants.CLIENT_ID_HEADER;
import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
@RequiredArgsConstructor
public class RestConsumerConfig {

    private final HttpClientProperties clientProperties;
    private final WebClientLoggingFilter webClientLoggingFilter;
    private final AuthTokenRenexFilter authTokenRenexFilter;

    @Bean
    public ModelMapper buildModelMapper() {
        return new ModelMapper();
    }

    @Bean("ApiPromoterClient")
    public WebClient buildClientPromoter(@Value("${adapter.rest-consumer.apis.promoter.auth-token}") String token) {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, String.format(AUTH_BEARER_VALUE, token))
                .exchangeStrategies(defineStrategy())
                .clientConnector(getClientHttpConnector())
                .filter(webClientLoggingFilter)
                .build();
    }

    @Bean("ApiConnectClient")
    public WebClient buildClientApiConnect(@Value("${adapter.rest-consumer.apis.api-connect.client-id}") String clientId) {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(CLIENT_ID_HEADER, clientId)
                .exchangeStrategies(defineStrategy())
                .clientConnector(getClientHttpConnector())
                .filter(webClientLoggingFilter)
                .build();
    }

    @Bean("nom151Client")
    public WebClient buildNom151Client() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE + ";charset=utf-8")
                .exchangeStrategies(defineStrategy())
                .clientConnector(getClientHttpConnector())
                .filter(webClientLoggingFilter)
                .build();
    }

    @Bean("clientGetFiles")
    public WebClient buildClientGetFiles(WebClient.Builder builder) {
        return builder
                .exchangeStrategies(defineStrategy())
                .clientConnector(getClientHttpConnector())
                .build();
    }

    @Bean("ApiRenexClient")
    public WebClient buildClientRenex() {
        return WebClient.builder()
                .clientConnector(getClientHttpConnector())
                .filter(webClientLoggingFilter)
                .filter(authTokenRenexFilter)
                .build();
    }

    @Bean("ocrClient")
    public WebClient buildOcrClient() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(getClientHttpConnector())
                .filter(webClientLoggingFilter)
                .build();
    }

    private ExchangeStrategies defineStrategy() {
        int maxBytes = clientProperties.getMemory() * 1024 * 1024;
        return ExchangeStrategies.builder()
                .codecs(config -> config.defaultCodecs().maxInMemorySize(maxBytes))
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
        int idleTimeout = (int) (timeout * 0.8);
        return new ReactorClientHttpConnector(HttpClient.create()
                .compress(true)
                .keepAlive(true)
                .option(CONNECT_TIMEOUT_MILLIS, timeout)
                .responseTimeout(Duration.ofMillis(timeout))
                .secure(sslContextSpec -> sslContextSpec.sslContext(getBuildSslContext()).handshakeTimeoutMillis(timeout))
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                    connection.addHandlerLast(new IdleStateHandler(idleTimeout, idleTimeout, 0, MILLISECONDS));
                }));
    }

}