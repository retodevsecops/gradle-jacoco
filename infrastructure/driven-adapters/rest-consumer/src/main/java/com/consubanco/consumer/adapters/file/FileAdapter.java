package com.consubanco.consumer.adapters.file;

import com.consubanco.consumer.adapters.file.dto.GetCNCALetterRequestDTO;
import com.consubanco.consumer.adapters.file.dto.GetCNCALetterResponseDTO;
import com.consubanco.consumer.adapters.file.properties.GetCNCALetterApiProperties;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.gateways.FileGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.file.message.FileTechnicalMessage.API_ERROR;

@Service
public class FileAdapter implements FileGateway {

    private final WebClient clientHttp;
    private final GetCNCALetterApiProperties getCNCALetterApiProperties;

    public FileAdapter(final @Qualifier("ApiConnectClient") WebClient clientHttp,
                       final GetCNCALetterApiProperties getCNCALetterApiProperties) {
        this.clientHttp = clientHttp;
        this.getCNCALetterApiProperties = getCNCALetterApiProperties;
    }

    @Override
    public Mono<String> getContentCNCALetter(String loanId) {
        GetCNCALetterRequestDTO requestDTO = new GetCNCALetterRequestDTO("", loanId);
        return this.clientHttp.post()
                .uri(getCNCALetterApiProperties.getEndpoint())
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(GetCNCALetterResponseDTO.class)
                .map(GetCNCALetterResponseDTO::getData)
                .filter(data -> data.getFiles() != null && !data.getFiles().isEmpty())
                .map(data -> data.getFiles().get(0).getBase64())
                .onErrorMap(error -> ExceptionFactory.buildTechnical(error, API_ERROR));
    }

}
