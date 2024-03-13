package com.consubanco.consumer.adapters.file;

import com.consubanco.consumer.adapters.file.dto.GetCNCALetterRequestDTO;
import com.consubanco.consumer.adapters.file.dto.GetCNCALetterResponseDTO;
import com.consubanco.consumer.adapters.file.properties.GetCNCALetterApiProperties;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateways.FileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.consubanco.model.entities.file.message.FileTechnicalMessage.API_ERROR;

@Service
public class FileAdapter implements FileRepository {

    private final WebClient clientHttp;
    private final ModelMapper modelMapper;
    private final GetCNCALetterApiProperties getCNCALetterApiProperties;

    public FileAdapter(final @Qualifier("ApiConnectClient") WebClient clientHttp,
                       final ModelMapper modelMapper,
                       final GetCNCALetterApiProperties getCNCALetterApiProperties) {
        this.clientHttp = clientHttp;
        this.modelMapper = modelMapper;
        this.getCNCALetterApiProperties = getCNCALetterApiProperties;
    }

    @Override
    public Mono<File> getCNCALetter(String accountNumber) {
        GetCNCALetterRequestDTO requestDTO = new GetCNCALetterRequestDTO("", accountNumber);
        return this.clientHttp.post()
                .uri(getCNCALetterApiProperties.getEndpoint())
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(GetCNCALetterResponseDTO.class)
                .map(response -> modelMapper.map(response.getData().getFiles().get(0), File.class))
                .onErrorMap(error -> ExceptionFactory.buildTechnical(error, API_ERROR));
    }

    private Optional<GetCNCALetterResponseDTO.FileResponseDTO> validateResponse(GetCNCALetterResponseDTO response) {
        return Optional.ofNullable(response)
                .map(GetCNCALetterResponseDTO::getData)
                .map(GetCNCALetterResponseDTO.CncaLetterResponseBO::getFiles)
                .filter(files -> !files.isEmpty())
                .map(files -> files.get(0));
    }

}
