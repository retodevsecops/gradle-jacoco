package com.consubanco.model.entities.document.gateway;

import reactor.core.publisher.Mono;

import java.util.List;

public interface PdfDocumentGateway {
    Mono<String> generatePdfWithImages(List<String> imagesInBase64);
}
