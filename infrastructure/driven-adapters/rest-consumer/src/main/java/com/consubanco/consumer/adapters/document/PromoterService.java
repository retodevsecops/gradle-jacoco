package com.consubanco.consumer.adapters.document;

import com.consubanco.consumer.adapters.document.dto.SearchInterlocutorReqDTO;
import com.consubanco.consumer.adapters.document.properties.PayloadApisProperties;
import com.consubanco.logger.CustomLogger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.file.message.FileTechnicalMessage.SEARCH_INTERLOCUTOR_ERROR;

@Service
public class PromoterService {

    private final CustomLogger logger;
    private final WebClient apiConnectClient;
    private final PayloadApisProperties apis;

    public PromoterService(final @Qualifier("ApiConnectClient") WebClient apiConnectClient,
                           final PayloadApisProperties apisProperties,
                           final CustomLogger logger) {
        this.apiConnectClient = apiConnectClient;
        this.apis = apisProperties;
        this.logger = logger;
    }

    @Cacheable("promoters")
    public Mono<Map<String, Object>> getPromoterById(String bpId) {
        return Mono.just(apis.getApiConnect().getApplicationId())
                .map(applicationId -> new SearchInterlocutorReqDTO(applicationId, bpId))
                .flatMap(this::searchInterlocutor)
                .doOnNext(data -> logger.info("The promoter information has been consulted", data));
    }

    private Mono<Map<String, Object>> searchInterlocutor(SearchInterlocutorReqDTO request) {
        return this.apiConnectClient.post()
                .uri(apis.getApiConnect().getApiSearchInterlocutor())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .filter(response -> getResponseCode(response).equals("200"))
                .flatMap(this::getDataInterlocutor)
                .onErrorMap(throwTechnicalError(SEARCH_INTERLOCUTOR_ERROR));
    }

    private String getResponseCode(Map<String, Object> response) {
        Map<String, Object> resBO = (Map<String, Object>) response.get("searchInterlocutorResBO");
        return (String) resBO.get("code");
    }

    private Mono<Map<String, Object>> getDataInterlocutor(Map<String, Object> response) {
        return Mono.just(response.get("searchInterlocutorResBO"))
                .map(resBO -> (Map<String, Object>) resBO)
                .map(map -> (List<Map<String, Object>>) map.get("people"))
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0));
    }

}
