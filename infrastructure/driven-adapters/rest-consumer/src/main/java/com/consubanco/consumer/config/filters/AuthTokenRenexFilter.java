package com.consubanco.consumer.config.filters;

import com.consubanco.consumer.commons.Constants;
import com.consubanco.logger.CustomLogger;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import static com.consubanco.consumer.commons.Constants.AUTH_BEARER_VALUE;

@Configuration
public class AuthTokenRenexFilter implements ExchangeFilterFunction {

    private static final String NOT_TOKEN_PROVIDER = "Credentials are not an instance of IdTokenProvider.";
    private static final String UNABLE_ACCESS_TOKEN = "Unable to obtain new access token";
    private static final String ERROR_MESSAGE = "Error generating the renex authentication token.";
    private static final String SUCCESS_MESSAGE = "Token was generated for Renex and expire in %s";
    private static final String DURATION_TOKEN_MESSAGE = "The cache for renex authorization token has a %s minutes expiration time.";
    private static final String ERROR_CACHE = "Error loading renex token into cache";

    private final CustomLogger logger;
    private Cache<String, String> cache;
    private final String audience;

    public AuthTokenRenexFilter(@Value("${adapter.rest-consumer.apis.renex.audience:renovacion-dev}") String audience,
                                CustomLogger logger) {
        this.logger = logger;
        this.audience = audience;
        loadTokenInCache();
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return getAuthToken()
                .map(token -> injectTokenToRequest(request, token))
                .flatMap(next::exchange)
                .doOnError(error -> logger.error(ERROR_MESSAGE, error));
    }

    private Mono<String> getAuthToken() {
        return Mono.justOrEmpty(cache.getIfPresent(Constants.TOKEN_RENEX_CACHE_KEY))
                .switchIfEmpty(generateToken());
    }

    private Mono<String> generateToken() {
        return getAccessToken()
                .doOnNext(token -> logger.info(String.format(SUCCESS_MESSAGE, token.getExpirationTime())))
                .doOnNext(this::addTokenInCache)
                .map(AccessToken::getTokenValue);
    }

    private void defineCache(Date expirationTime) {
        Instant expirationInstant = expirationTime.toInstant();
        Duration duration = Duration.between(Instant.now(), expirationInstant);
        this.cache = Caffeine.newBuilder().expireAfterWrite(duration).build();
        logger.info(String.format(DURATION_TOKEN_MESSAGE, duration.toMinutes()));
    }

    private Mono<AccessToken> getAccessToken() {
        return Mono.create(sink -> {
            try {
                IdTokenProvider tokenProvider = getTokenProvider();
                IdTokenCredentials idTokenCredentials = buildTokenCredentials(tokenProvider);
                AccessToken accessToken = idTokenCredentials.refreshAccessToken();
                if (Objects.isNull(accessToken)) sink.error(new RuntimeException(UNABLE_ACCESS_TOKEN));
                sink.success(accessToken);
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    private IdTokenCredentials buildTokenCredentials(IdTokenProvider tokenProvider) {
        return IdTokenCredentials.newBuilder()
                .setIdTokenProvider(tokenProvider)
                .setTargetAudience(this.audience)
                .build();
    }

    private IdTokenProvider getTokenProvider() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault();
        if (googleCredentials instanceof IdTokenProvider idTokenProvider) return idTokenProvider;
        throw new IllegalArgumentException(NOT_TOKEN_PROVIDER);
    }

    private ClientRequest injectTokenToRequest(ClientRequest request, String token) {
        return ClientRequest.from(request)
                .header(HttpHeaders.AUTHORIZATION, String.format(AUTH_BEARER_VALUE, token))
                .build();
    }

    private void loadTokenInCache() {
        this.getAccessToken()
                .doOnNext(accessToken -> defineCache(accessToken.getExpirationTime()))
                .doOnNext(this::addTokenInCache)
                .doOnError(error -> logger.error(ERROR_CACHE, error))
                .subscribe();
    }

    private void addTokenInCache(AccessToken accessToken) {
        this.cache.put(Constants.TOKEN_RENEX_CACHE_KEY, accessToken.getTokenValue());
    }

}
