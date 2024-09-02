package com.consubanco.api.services.management;

import com.consubanco.api.commons.util.HttpResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ManagementHandler {

    private final CacheManager cacheManager;

    public Mono<ServerResponse> cleanCache(ServerRequest request) {
        return Flux.fromIterable(cacheManager.getCacheNames())
                .filter(name -> cacheManager.getCache(name) != null)
                .map(key -> {
                    Cache cache = cacheManager.getCache(key);
                    if (cache != null) cache.clear();
                    return key;
                })
                .collectList()
                .map(list -> Map.of("items_removed", list))
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> getCacheByName(ServerRequest request) {
        String cacheName = request.pathVariable("name");
        return Flux.fromIterable(cacheManager.getCacheNames())
                .filter(name -> cacheManager.getCache(name) != null)
                .map(key -> {
                    Cache cache = cacheManager.getCache(key);
                    if (cache != null) cache.clear();
                    return key;
                })
                .collectList()
                .map(list -> Map.of("items_removed", list))
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> getItemsCache(ServerRequest request) {
        return Flux.fromIterable(cacheManager.getCacheNames())
                .collectList()
                .map(list -> Map.of("items_in_cache", list))
                .flatMap(HttpResponseUtil::ok);
    }
}
