package com.consubanco.consumer.adapters.document;

import com.consubanco.consumer.adapters.document.dto.SearchInterlocutorReqDTO;
import com.consubanco.consumer.adapters.document.properties.ApisProperties;
import com.consubanco.logger.CustomLogger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.API_SEARCH_INTERLOCUTOR_ERROR;

@Service
public class PromoterApiConsumer {

    private final CustomLogger logger;
    private final WebClient apiConnectClient;
    private final ApisProperties apis;

    public PromoterApiConsumer(final @Qualifier("ApiConnectClient") WebClient apiConnectClient,
                               final ApisProperties apisProperties,
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
                .filter(response -> getResponseCode(response).equals(HttpStatus.OK.value()))
                .flatMap(this::getDataInterlocutor)
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_SEARCH_INTERLOCUTOR_ERROR))
                .onErrorMap(throwTechnicalError(API_SEARCH_INTERLOCUTOR_ERROR));
    }

    @SuppressWarnings("unchecked")
    private Integer getResponseCode(Map<String, Object> response) {
        Map<String, Object> resBO = (Map<String, Object>) response.get("searchInterlocutorResBO");
        return Integer.parseInt((String) resBO.get("code"));
    }

    @SuppressWarnings("unchecked")
    private Mono<Map<String, Object>> getDataInterlocutor(Map<String, Object> response) {
        return Mono.just(response.get("searchInterlocutorResBO"))
                .map(resBO -> (Map<String, Object>) resBO)
                .map(map -> (List<Map<String, Object>>) map.get("people"))
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0));
    }

}
