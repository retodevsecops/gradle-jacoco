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

    private final CustomLogger logger;
    private Cache<String, String> cache;
    private final String audience;

    public AuthTokenRenexFilter(@Value("adapter.rest-consumer.apis.renex.audience") String audience,
                                CustomLogger logger) {
        this.logger = logger;
        this.audience = audience;
        loadTokenInCache();
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return getAuthToken()
                .doOnNext(e -> logger.info("se obtuvo el token de autenticacion."))
                .map(token -> injectTokenToRequest(request, token))
                .flatMap(next::exchange)
                .doOnError(error -> logger.error("Error generating the renex authentication token.", error));
    }

    private Mono<String> getAuthToken() {
        return Mono.just(Constants.TOKEN_RENEX_CACHE_KEY)
                .map(cache::getIfPresent)
                .doOnNext(e -> System.out.println("El token se obtuvo de la cache"))
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
        this.cache = Caffeine.newBuilder().expireAfterWrite(duration).build();
        logger.info("The cache for renex authorization token has a " + duration.toMinutes() + " minutes expiration time.");
    }

    private Mono<AccessToken> getAccessToken() {
        return Mono.create(sink -> {
            try {
                IdTokenProvider tokenProvider = getTokenProvider();
                IdTokenCredentials idTokenCredentials = buildTokenCredentials(tokenProvider);
                AccessToken accessToken = idTokenCredentials.refreshAccessToken();
                if(Objects.isNull(accessToken)){
                    logger.info("ojo! el token que se intento generar es null");
                    sink.error(new RuntimeException("No se pudo obtener un nuevo token de acceso"));
                }else {
                    logger.info("el token que se genero es valido y no es null");
                    sink.success(accessToken);
                }
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
        if (googleCredentials instanceof IdTokenProvider)
            return (IdTokenProvider) googleCredentials;
        throw new IllegalArgumentException("Credentials are not an instance of IdTokenProvider.");
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
                .doOnError(error -> logger.info("Error loading renex token into cache", error))
                .subscribe();
    }

    private void addTokenInCache(AccessToken accessToken) {
        this.cache.put(Constants.TOKEN_RENEX_CACHE_KEY, accessToken.getTokenValue());
    }

}
