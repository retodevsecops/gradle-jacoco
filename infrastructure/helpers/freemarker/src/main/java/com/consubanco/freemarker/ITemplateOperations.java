package com.consubanco.freemarker;

import reactor.core.publisher.Mono;

public interface ITemplateOperations {
    <T> Mono<T> processTemplate(String templateAsString, Object data, Class<T> cls);
}
