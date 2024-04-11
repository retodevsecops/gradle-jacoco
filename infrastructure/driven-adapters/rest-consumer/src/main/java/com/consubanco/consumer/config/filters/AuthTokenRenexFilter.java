package com.consubanco.consumer.config.filters;

import com.consubanco.consumer.commons.Constants;
import com.consubanco.logger.CustomLogger;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static com.consubanco.consumer.commons.Constants.AUTH_BEARER_VALUE;

@Configuration
public class AuthTokenRenexFilter implements ExchangeFilterFunction {

    private CustomLogger logger;
    private Cache<String, String> cache;

    public AuthTokenRenexFilter(CustomLogger logger) {
        this.logger = logger;
        loadTokenInCache();
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return getAuthToken()
                .map(token -> injectTokenToRequest(request, token))
                .flatMap(next::exchange);
    }

    private Mono<String> getAuthToken() {
        return Mono.just(Constants.TOKEN_RENEX_CACHE_KEY)
                .map(cache::getIfPresent)
                .switchIfEmpty(generateToken());
    }

    private Mono<String> generateToken() {
        return getAccessToken()
                .doOnNext(token -> logger.info("Token was generated for Renex and expire in " + token.getExpirationTime()))
                .doOnNext(this::addTokenInCache)
                .map(AccessToken::getTokenValue);
    }

    private void defineCache(Date expirationTime) {
        Instant expirationInstant = expirationTime.toInstant();
        Duration duration = Duration.between(Instant.now(), expirationInstant);
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(duration)
                .build();
        logger.info("The cache for renex authorization token has a " + duration.toMinutes() + " minutes expiration time.");
    }

    private Mono<AccessToken> getAccessToken() {
        return Mono.create(sink -> {
            try {
                GoogleCredentials googleCredentials = ServiceAccountCredentials.getApplicationDefault();
                googleCredentials.refreshIfExpired();
                sink.success(googleCredentials.getAccessToken());
            } catch (Exception e) {
                sink.error(e);
            }
        });
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
                .subscribe();
    }

    private void addTokenInCache(AccessToken accessToken) {
        this.cache.put(Constants.TOKEN_RENEX_CACHE_KEY, accessToken.getTokenValue());
    }

}
