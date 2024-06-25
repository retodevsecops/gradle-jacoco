package com.consubanco.consumer.adapters.ocr;

import com.consubanco.consumer.adapters.ocr.dto.GetMetadataReqDTO;
import com.consubanco.consumer.adapters.ocr.dto.GetMetadataResDTO;
import com.consubanco.consumer.adapters.ocr.dto.NotifyDocumentReqDTO;
import com.consubanco.consumer.adapters.ocr.dto.NotifyDocumentResDTO;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.ocr.constant.OcrDocumentType;
import com.consubanco.model.entities.ocr.gateway.OcrDocumentGateway;
import com.consubanco.model.entities.ocr.message.OcrTechnicalMessage;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.ocr.message.OcrMessage.apiError;
import static com.consubanco.model.entities.ocr.message.OcrMessage.notMetadata;
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
    public Mono<Duration> getDelayTime() {
        return Mono.just(apiProperties.initialDelayInSeconds());
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
    public Mono<List<OcrDataVO>> getAnalysisData(String analysisId) {
        return Mono.defer(() -> getMetadata(analysisId))
                .filter(metadata -> !metadata.getListOfExtractionFields().isEmpty())
                .map(GetMetadataResDTO::extractionFieldsToModel);
    }

    private Mono<GetMetadataResDTO> getMetadata(String analysisId) {
        return Mono.defer(() -> callApiToGetMetadata(analysisId))
                .filter(metadata -> Objects.nonNull(metadata.getData()))
                .switchIfEmpty(ExceptionFactory.monoTechnicalError(notMetadata(analysisId), NOT_METADATA))
                .retryWhen(defineRetryStrategy())
                .doOnError(throwable -> logger.error("Failed to get analysis data "+analysisId+" after retries", throwable));
    }

    public Mono<GetMetadataResDTO> callApiToGetMetadata(String transactionId) {
        GetMetadataReqDTO request = new GetMetadataReqDTO(apiProperties.getApplicationId(), transactionId);
        logger.info(apiProperties.getApiGetDataDocument() + ": body request to get ocr document data", request);
        return ocrClient.post()
                .uri(apiProperties.getApiGetDataDocument())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GetMetadataResDTO.class)
                .onErrorMap(WebClientResponseException.class, error -> customError(error, API_GET_METADATA_RESPONSE_ERROR))
                .onErrorMap(e -> !(e instanceof  TechnicalException), throwTechnicalError(API_GET_METADATA_ERROR))
                .doOnError(error -> logger.error("Error when get ocr document data by transactionId " + transactionId, error));
    }

    private TechnicalException customError(WebClientResponseException error, OcrTechnicalMessage message) {
        String api = apiProperties.getApiNotifyDocument();
        String status = error.getStatusText();
        String body = error.getResponseBodyAsString();
        String detail = apiError(api, status, body);
        return buildTechnical(detail, message);
    }

    private RetryBackoffSpec defineRetryStrategy() {
        return Retry.backoff(apiProperties.getMaxRetries(), apiProperties.retryDelayInSeconds())
                .maxBackoff(apiProperties.maxRetryDelayInMinutes())
                .doBeforeRetry(signal -> logger.info("Retry attempt #" + signal.totalRetriesInARow() + " by error: " + signal.failure().getMessage()));
    }

}