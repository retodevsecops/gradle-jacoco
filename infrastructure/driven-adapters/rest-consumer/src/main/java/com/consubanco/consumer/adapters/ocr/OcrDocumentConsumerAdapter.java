package com.consubanco.consumer.adapters.ocr;

import com.consubanco.consumer.adapters.ocr.dto.NotifyDocumentReqDTO;
import com.consubanco.consumer.adapters.ocr.dto.NotifyDocumentResDTO;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.message.OcrMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.ocr.message.OcrTechnicalMessage.API_NOTIFY_ERROR;
import static com.consubanco.model.entities.ocr.message.OcrTechnicalMessage.API_NOTIFY_RESPONSE_ERROR;

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
        logger.info("Ocr document notification request detail", request);
        return ocrClient.post()
                .uri(apiProperties.getApiNotifyDocument())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NotifyDocumentResDTO.class)
                .map(NotifyDocumentResDTO::getTransactionId)
                .onErrorMap(WebClientResponseException.class, this::customError)
                .onErrorMap(e -> !(e instanceof  TechnicalException), throwTechnicalError(API_NOTIFY_ERROR));
    }

    private TechnicalException customError(WebClientResponseException error) {
        String api = apiProperties.getApiNotifyDocument();
        String status = error.getStatusText();
        String body = error.getResponseBodyAsString();
        String detail = OcrMessage.apiError(api, status, body);
        return buildTechnical(detail, API_NOTIFY_RESPONSE_ERROR);
    }

    @Override
    public Mono<Map<String, Object>> getAnalysisData(String analysisId) {
        return null;
    }

}