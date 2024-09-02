package com.consubanco.caffeine;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class CacheOperations {

    private final CaffeineCacheManager cacheManager;

    public CacheOperations(@Qualifier("caffeineCacheManager") final CacheManager cacheManager) {
        this.cacheManager = (CaffeineCacheManager) cacheManager;
    }

    public Mono<List<String>> items() {
        return Flux.fromIterable(cacheManager.getCacheNames())
                .flatMap(name -> {
                    CaffeineCache cache = (CaffeineCache) cacheManager.getCache(name);
                    if (Objects.isNull(cache)) return Mono.empty();
                    if (cache.getNativeCache().asMap().isEmpty()) return Mono.empty();
                    return Mono.just(name);
                })
                .collectList();
    }

    public Mono<List<String>> clean() {
        return Flux.fromIterable(cacheManager.getCacheNames())
                .filter(name -> Objects.nonNull(cacheManager.getCache(name)))
                .map(key -> {
                    Cache cache = cacheManager.getCache(key);
                    if (cache != null) cache.clear();
                    return key;
                })
                .collectList();
    }

    public Mono<ConcurrentMap<Object, Object>> getObjectsByItem(String item) {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache(item);
        if (Objects.isNull(cache)) return Mono.empty();
        return Mono.just(cache.getNativeCache().asMap());
    }

    public Mono<ConcurrentMap<Object, Object>> cleanByItem(String item) {
        return Mono.defer(() -> {
            CaffeineCache cache = (CaffeineCache) cacheManager.getCache(item);
            if (Objects.isNull(cache)) return Mono.empty();
            ConcurrentMap<Object, Object> dataInCache = new ConcurrentHashMap<>(cache.getNativeCache().asMap());
            cache.clear();
            return Mono.just(dataInCache);
        });
    }

}
