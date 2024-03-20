package com.consubanco.consumer.adapters.document;

import com.consubanco.consumer.adapters.document.dto.GenerateDocumentRequestDTO;
import com.consubanco.consumer.adapters.document.dto.GenerateDocumentResponseDTO;
import com.consubanco.consumer.adapters.document.properties.GenerateDocumentApiProperties;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.document.Document;
import com.consubanco.model.entities.document.gateway.DocumentGateway;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.API_ERROR;

@Service
public class DocumentConsumerAdapter implements DocumentGateway {

    private final WebClient clientHttp;
    private final GenerateDocumentApiProperties generateDocumentApiProperties;

    public DocumentConsumerAdapter(final @Qualifier("ApiPromoterClient") WebClient clientHttp,
                                   final ModelMapper modelMapper,
                                   final GenerateDocumentApiProperties generateDocumentApiProperties) {
        this.clientHttp = clientHttp;
        this.generateDocumentApiProperties = generateDocumentApiProperties;
    }

    @Override
    public Mono<String> generate(List<Document> documents, Map<String, Object> payload) {
        return this.clientHttp.post()
                .uri(generateDocumentApiProperties.getEndpoint())
                .bodyValue(new GenerateDocumentRequestDTO(documents, payload))
                .retrieve()
                .bodyToMono(GenerateDocumentResponseDTO.class)
                .map(GenerateDocumentResponseDTO::getPublicUrl)
                .onErrorMap(error -> ExceptionFactory.buildTechnical(error, API_ERROR));
    }

}
