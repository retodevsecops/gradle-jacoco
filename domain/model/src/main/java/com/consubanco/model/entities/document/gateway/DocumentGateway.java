package com.consubanco.model.entities.document.gateway;

import com.consubanco.model.entities.document.Document;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface DocumentGateway {
    Mono <String> generate(List<Document> documents, Map<String, Object> payload);
}
