package com.consubanco.consumer.adapters.file;

import com.consubanco.consumer.adapters.file.dto.GenerateDocumentRequestDTO;
import com.consubanco.consumer.adapters.file.dto.GenerateDocumentResponseDTO;
import com.consubanco.consumer.adapters.file.dto.GetCNCALetterRequestDTO;
import com.consubanco.consumer.adapters.file.dto.GetCNCALetterResponseDTO;
import com.consubanco.consumer.adapters.file.properties.FileApisProperties;
import com.consubanco.freemarker.ITemplateOperations;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.file.gateways.FileGateway;
import com.consubanco.model.entities.file.vo.AttachmentVO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.monoTechnicalError;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.*;

@Service
public class FileAdapter implements FileGateway {

    private final CustomLogger logger;
    private final WebClient apiConnectClient;
    private final WebClient apiPromoterClient;
    private final FileApisProperties apis;
    private final ITemplateOperations templateOperations;

    public FileAdapter(final @Qualifier("ApiConnectClient") WebClient apiConnectClient,
                       final @Qualifier("ApiPromoterClient") WebClient apiPromoterClient,
                       final FileApisProperties apisProperties,
                       final CustomLogger customLogger,
                       final ITemplateOperations templateOperations) {
        this.apiConnectClient = apiConnectClient;
        this.apiPromoterClient = apiPromoterClient;
        this.logger = customLogger;
        this.apis = apisProperties;
        this.templateOperations = templateOperations;
    }

    @Override
    public Mono<String> getContentCNCALetter(String loanId) {
        GetCNCALetterRequestDTO requestDTO = new GetCNCALetterRequestDTO(apis.getApplicationId(), loanId);
        return this.apiConnectClient.post()
                .uri(apis.getCNCAApiEndpoint())
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(GetCNCALetterResponseDTO.class)
                .map(GetCNCALetterResponseDTO::getData)
                .flatMap(this::getFileAsBase64)
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(API_ERROR));
    }

    @Override
    public Mono<String> generate(String document, Map<String, Object> payload) {
        return generate(List.of(document), null, payload);
    }

    @Override
    public Mono<Map<String, Object>> buildPayload(String template) {
        return templateOperations.processTemplate(template, null, Map.class)
                .map(map -> (Map<String, Object>) map);
    }

    @Override
    public Mono<String> generate(List<String> documents, List<AttachmentVO> attachments, Map<String, Object> payload) {
        logger.info("Payload with all data ", payload);
        return this.apiPromoterClient.post()
                .uri(apis.generateDocumentApiEndpoint())
                .bodyValue(new GenerateDocumentRequestDTO(documents, attachments, payload))
                .retrieve()
                .bodyToMono(GenerateDocumentResponseDTO.class)
                .map(GenerateDocumentResponseDTO::getPublicUrl)
                .onErrorMap(throwTechnicalError(API_PROMOTER_ERROR));
    }

    private Mono<String> getFileAsBase64(GetCNCALetterResponseDTO.Data data) {
        return Mono.justOrEmpty(data.getFiles())
                .filter(files -> !files.isEmpty())
                .map(files -> files.get(0).getBase64())
                .switchIfEmpty(monoTechnicalError(data.getResponse(), CNCA_LETTER_ERROR));
    }

}
