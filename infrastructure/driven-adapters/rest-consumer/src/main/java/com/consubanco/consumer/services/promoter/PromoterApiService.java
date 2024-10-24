package com.consubanco.consumer.services.promoter;

import com.consubanco.consumer.services.promoter.dto.BranchesByPromoterReqDTO;
import com.consubanco.consumer.services.promoter.dto.SearchInterlocutorReqDTO;
import com.consubanco.consumer.services.promoter.util.SearchInterlocutorResUtil;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.List;
import java.util.Map;

import static com.consubanco.consumer.commons.ClientExceptionFactory.requestError;
import static com.consubanco.consumer.services.promoter.util.BranchesByPromoterResUtil.*;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.document.message.DocumentTechnicalMessage.*;

@Service
public class PromoterApiService {

    private final CustomLogger logger;
    private final WebClient apiConnectClient;
    private final PromoterApisProperties apis;

    public PromoterApiService(final @Qualifier("ApiConnectClient") WebClient apiConnectClient,
                              final PromoterApisProperties apisProperties,
                              final CustomLogger logger) {
        this.apiConnectClient = apiConnectClient;
        this.apis = apisProperties;
        this.logger = logger;
    }

    @Cacheable("promoters")
    public Mono<Map<String, Object>> getPromoterById(String promoterBpId) {
        return Mono.zip(searchInterlocutor(promoterBpId), getBranchesByPromoter(promoterBpId))
                .map(TupleUtils.function(this::branchesToPromoter))
                .doOnNext(data -> logger.info("The promoter information has been consulted", data));
    }

    private Mono<Map<String, Object>> searchInterlocutor(String promoterBpId) {
        return this.apiConnectClient.post()
                .uri(apis.getApiSearchInterlocutor())
                .bodyValue(new SearchInterlocutorReqDTO(apis.getApplicationId(), promoterBpId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .filter(SearchInterlocutorResUtil::checkIfSuccessResponse)
                .flatMap(SearchInterlocutorResUtil::getDataInterlocutor)
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_SEARCH_INTERLOCUTOR_ERROR))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(API_SEARCH_INTERLOCUTOR_ERROR));
    }

    private Mono<List<Map<String, Object>>> getBranchesByPromoter(String promoterBpId) {
        String endpoint = apis.getApiBranchesPromoter();
        return this.apiConnectClient.post()
                .uri(endpoint)
                .bodyValue(new BranchesByPromoterReqDTO(apis.getApplicationId(), promoterBpId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .flatMap(response -> {
                    if(Boolean.TRUE.equals(checkIfSuccessResponse(response))) return getBranches(response);
                    String cause = endpoint.concat(" api response: ").concat(response.toString());
                    return ExceptionFactory.monoTechnicalError(cause, API_BRANCHES_BY_PROMOTER_ERROR);
                })
                .onErrorMap(WebClientRequestException.class, error -> requestError(error, API_REQUEST_ERROR))
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_BRANCHES_BY_PROMOTER_ERROR))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(API_BRANCHES_BY_PROMOTER_ERROR));
    }

    private Map<String, Object> branchesToPromoter(Map<String, Object> promoter, List<Map<String, Object>> branches) {
        promoter.put(BRANCHES, branches);
        return promoter;
    }

}
