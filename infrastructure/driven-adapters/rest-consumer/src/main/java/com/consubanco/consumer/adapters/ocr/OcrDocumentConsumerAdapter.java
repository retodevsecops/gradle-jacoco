package com.consubanco.consumer.adapters.ocr;

import com.consubanco.consumer.adapters.ocr.dto.GetMetadataReqDTO;
import com.consubanco.consumer.adapters.ocr.dto.GetMetadataResDTO;
import com.consubanco.consumer.adapters.ocr.dto.NotifyDocumentReqDTO;
import com.consubanco.consumer.adapters.ocr.dto.NotifyDocumentResDTO;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.message.OcrMessage;
import com.consubanco.model.entities.ocr.message.OcrTechnicalMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.ocr.message.OcrTechnicalMessage.*;

@Service
public class OcrDocumentConsumerAdapter implements OcrDocumentGateway {

    private final CustomLogger logger;
    private final WebClient ocrClient;
    private final OcrApiProperties apiProperties;

    public OcrDocumentConsumerAdapter(final @Qualifier("ocrClient") WebClient ocrClient,
                                      final CustomLogger logger,
                                      final OcrApiProperties ocrApiProperties) {
        this.logger = logger;
        this.ocrClient = ocrClient;
        this.apiProperties = ocrApiProperties;
    }

    @Override
    public Mono<String> notifyDocumentForAnalysis(String storageRoute, OcrDocumentType ocrDocumentType) {
        NotifyDocumentReqDTO request = new NotifyDocumentReqDTO(apiProperties.getApplicationId(), storageRoute, ocrDocumentType.getType());
        logger.info(apiProperties.getApiNotifyDocument() + ": body request to notify ocr document", request);
        return ocrClient.post()
                .uri(apiProperties.getApiNotifyDocument())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NotifyDocumentResDTO.class)
                .map(NotifyDocumentResDTO::getTransactionId)
                .onErrorMap(WebClientResponseException.class, error -> customError(error, API_NOTIFY_RESPONSE_ERROR))
                .onErrorMap(e -> !(e instanceof  TechnicalException), throwTechnicalError(API_NOTIFY_ERROR));
    }

    @Override
    public Mono<Map<String, Object>> getAnalysisData(String analysisId) {
        return getMetadata(analysisId);
    }

    public Mono<Map<String, Object>> getMetadata(String transactionId) {
        GetMetadataReqDTO request = new GetMetadataReqDTO(apiProperties.getApplicationId(), transactionId);
        logger.info(apiProperties.getApiGetDataDocument() + ": body request to get ocr document data", request);
        return ocrClient.post()
                .uri(apiProperties.getApiGetDataDocument())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GetMetadataResDTO.class)
                .map(GetMetadataResDTO::getData)
                .onErrorMap(WebClientResponseException.class, error -> customError(error, API_GET_METADATA_RESPONSE_ERROR))
                .onErrorMap(e -> !(e instanceof  TechnicalException), throwTechnicalError(API_GET_METADATA_ERROR));
    }

    private TechnicalException customError(WebClientResponseException error, OcrTechnicalMessage message) {
        String api = apiProperties.getApiNotifyDocument();
        String status = error.getStatusText();
        String body = error.getResponseBodyAsString();
        String detail = OcrMessage.apiError(api, status, body);
        return buildTechnical(detail, message);
    }

}