package com.consubanco.consumer.adapters.file;

import com.consubanco.consumer.commons.Base64Util;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.file.gateways.FileConvertGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.ENCODED_ERROR;

@Service
public class FileConvertAdapter implements FileConvertGateway {

    private final WebClient clientGetFiles;

    public FileConvertAdapter(final @Qualifier("clientGetFiles") WebClient clientGetFiles) {
        this.clientGetFiles = clientGetFiles;
    }

    @Override
    public Mono<String> encodedFile(String fileUrl) {
        return clientGetFiles.get()
                .uri(fileUrl)
                .accept(MediaType.APPLICATION_PDF)
                .retrieve()
                .bodyToMono(Resource.class)
                .flatMap(Base64Util::resourceToBase64)
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(ENCODED_ERROR));
    }

}
