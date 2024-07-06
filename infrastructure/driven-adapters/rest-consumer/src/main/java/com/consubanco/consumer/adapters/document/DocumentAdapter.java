package com.consubanco.consumer.adapters.document;

import com.consubanco.consumer.adapters.document.dto.*;
import com.consubanco.consumer.adapters.document.properties.DocumentApisProperties;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.agreement.vo.AttachmentConfigVO;
import com.consubanco.model.entities.document.gateway.DocumentGateway;
import com.consubanco.model.entities.document.vo.GenerateDocumentVO;
import com.consubanco.model.entities.document.vo.PreviousDocumentVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.timeout.TimeoutException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.consubanco.consumer.adapters.document.dto.GetDocsPreviousApplicationResDTO.getResponseCode;
import static com.consubanco.consumer.adapters.document.dto.GetDocsPreviousApplicationResDTO.getResponseMessage;
import static com.consubanco.consumer.commons.ClientExceptionFactory.requestError;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.*;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.API_DOCS_PREVIOUS_ERROR;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.API_DOCS_PREVIOUS_TIMEOUT;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.*;

@Service
public class DocumentAdapter implements DocumentGateway {

    private final WebClient apiConnectClient;
    private final WebClient apiPromoterClient;
    private final DocumentApisProperties apis;
    private final ObjectMapper objectMapper;

    public DocumentAdapter(final @Qualifier("ApiConnectClient") WebClient apiConnectClient,
                           final @Qualifier("ApiPromoterClient") WebClient apiPromoterClient,
                           final DocumentApisProperties apisProperties,
                           final ObjectMapper objectMapper) {
        this.apiConnectClient = apiConnectClient;
        this.apiPromoterClient = apiPromoterClient;
        this.apis = apisProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<String> generateContentCNCALetter(String loanId) {
        GenerateCNCALetterReqDTO requestDTO = new GenerateCNCALetterReqDTO(apis.getApplicationId(), loanId);
        return this.apiConnectClient.post()
                .uri(apis.getCNCAApiEndpoint())
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(GenerateCNCALetterResDTO.class)
                .filter(GenerateCNCALetterResDTO::checkCNCAIfExists)
                .map(GenerateCNCALetterResDTO::getData)
                .map(GenerateCNCALetterResDTO.Data::getBase64)
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_ERROR))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(API_ERROR));
    }

    @Override
    public Integer validDaysCNCA() {
        return apis.getApiConnect().getValidDaysCnca();
    }

    @Override
    public Mono<String> generate(String document, Map<String, Object> payload) {
        return generate(GenerateDocumentVO.builder().documents(List.of(document)).build(), payload);
    }

    @Override
    public Mono<Map<String, String>> generateMultiple(List<String> documents, Map<String, Object> payload) {
        return this.apiPromoterClient.post()
                .uri(apis.generateDocumentApiEndpoint())
                .bodyValue(new GenerateDocumentRequestDTO(documents, payload))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_PROMOTER_ERROR))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(API_PROMOTER_ERROR));
    }

    @Override
    public Flux<PreviousDocumentVO> getDocsFromPreviousApplication(String previousApplicationId, List<AttachmentConfigVO> docs) {
        return this.apiConnectClient.post()
                .uri(apis.getApiConnect().getApiDocsPrevious())
                .bodyValue(new GetDocsPreviousApplicationReqDTO(apis.getApplicationId(), previousApplicationId, docs))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .flatMapMany(response -> {
                    Integer responseCode = getResponseCode(response);
                    if (responseCode.equals(HttpStatus.OK.value())) {
                        return objectMapper.convertValue(response, GetDocsPreviousApplicationResDTO.class).toDomainEntity();
                    }
                    String responseMessage = getResponseMessage(response);
                    String detail = "Api response with code %s: %s";
                    String cause = String.format(detail, responseCode, responseMessage);
                    return monoTechnicalError(cause, API_DOCS_PREVIOUS_ERROR);
                })
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_DOCS_PREVIOUS_ERROR))
                .onErrorMap(error -> !(error instanceof TechnicalException), error -> {
                    if (error.getCause() instanceof TimeoutException)
                        return buildTechnical(error.getCause(), API_DOCS_PREVIOUS_TIMEOUT);
                    return buildTechnical(error.getCause(), API_DOCS_PREVIOUS_ERROR);
                });
    }

    @Override
    public Mono<String> generate(GenerateDocumentVO generateDocumentVO, Map<String, Object> payload) {
        return this.apiPromoterClient.post()
                .uri(apis.generateDocumentApiEndpoint())
                .bodyValue(new GenerateDocumentRequestDTO(generateDocumentVO, payload))
                .retrieve()
                .bodyToMono(GenerateDocumentResponseDTO.class)
                .map(GenerateDocumentResponseDTO::getPublicUrl)
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_PROMOTER_ERROR))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(API_PROMOTER_ERROR));
    }

}
