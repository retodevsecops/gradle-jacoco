package com.consubanco.model.entities.document.gateway;

import reactor.core.publisher.Mono;

import java.util.List;

public interface PDFDocumentGateway {
    Mono<String> generatePdfWithImages(List<String> imagesInBase64);

    Mono<String> getPageFromPDF(String base64PDF, Integer page);

    Mono<String> merge(List<String> base64Documents);
}
