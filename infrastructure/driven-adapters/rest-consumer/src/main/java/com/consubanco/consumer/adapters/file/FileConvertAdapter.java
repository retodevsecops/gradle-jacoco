package com.consubanco.consumer.adapters.file;

import com.consubanco.consumer.commons.Base64Util;
import com.consubanco.consumer.config.dto.RestConsumerLogDTO;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.file.gateway.FileConvertGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.ENCODED_ERROR;

@Service
public class FileConvertAdapter implements FileConvertGateway {

    private final CustomLogger logger;
    private final WebClient clientGetFiles;

    public FileConvertAdapter(final @Qualifier("clientGetFiles") WebClient clientGetFiles,
                              final CustomLogger logger) {
        this.clientGetFiles = clientGetFiles;
        this.logger = logger;
    }

    @Override
    public Mono<String> getFileContentAsBase64(String fileUrl) {
        return clientGetFiles.get()
                .uri(uriBuilder -> buildUri(fileUrl))
                .retrieve()
                .bodyToMono(Resource.class)
                .flatMap(Base64Util::resourceToBase64)
                .doOnError(WebClientResponseException.class, error -> logger.error(new RestConsumerLogDTO(error)))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(ENCODED_ERROR));
    }

    private URI buildUri(String fileUrl) {
        try {
            return new URI(fileUrl);
        } catch (URISyntaxException exception) {
            throw new RuntimeException("Invalid URI: " + fileUrl, exception);
        }
    }

}
