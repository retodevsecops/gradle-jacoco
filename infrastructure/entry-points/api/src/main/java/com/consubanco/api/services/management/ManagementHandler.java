package com.consubanco.api.services.management;

import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.services.management.constants.ManagementParams;
import com.consubanco.caffeine.CacheOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ManagementHandler {

    private final CacheOperations cacheOperations;

    public Mono<ServerResponse> cleanCache(ServerRequest request) {
        return cacheOperations.clean()
                .map(items -> Map.of("items_removed", items))
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> getItemsCache(ServerRequest request) {
        return cacheOperations.items()
                .map(items -> Map.of("items_in_cache", items))
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> getObjectsByItem(ServerRequest request) {
        String item = request.pathVariable(ManagementParams.ITEM);
        return cacheOperations.getObjectsByItem(item)
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> cleanCacheByItem(ServerRequest request) {
        String item = request.pathVariable(ManagementParams.ITEM);
        return cacheOperations.cleanByItem(item)
                .map(objects -> Map.of("objects_removed", objects))
                .flatMap(HttpResponseUtil::ok);
    }

}
