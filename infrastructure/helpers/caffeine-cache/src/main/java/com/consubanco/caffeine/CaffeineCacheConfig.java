package com.consubanco.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CaffeineCacheConfig {

    private final CaffeineProperties caffeineProperties;

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(caffeineProperties.getMaxSizeElements())
                .expireAfterWrite(caffeineProperties.getExpireAfterWrite(), TimeUnit.MINUTES));
        cacheManager.setAllowNullValues(false);
        cacheManager.setAsyncCacheMode(true);
        return cacheManager;
    }

}
