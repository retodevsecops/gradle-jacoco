package com.consubanco.freemarker;

import reactor.core.publisher.Mono;

public interface ITemplateOperations {
    <T> Mono<T> process(String templateAsString, Object data, Class<T> cls);
    boolean validate(String templateAsString);
}
