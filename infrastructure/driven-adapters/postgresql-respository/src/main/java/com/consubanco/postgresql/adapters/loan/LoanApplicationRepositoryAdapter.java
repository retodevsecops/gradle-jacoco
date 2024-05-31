package com.consubanco.postgresql.adapters.loan;

import com.consubanco.logger.CustomLogger;
import com.consubanco.model.commons.exception.TechnicalException;
import com.consubanco.model.entities.loan.LoanApplication;
import com.consubanco.model.entities.loan.gateway.LoanApplicationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.Map;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.loan.message.LoanTechnicalMessage.*;

@Service
@RequiredArgsConstructor
public class LoanApplicationRepositoryAdapter implements LoanApplicationRepository {

    private final CustomLogger logger;
    private final LoanApplicationDataRepository dataRepository;
    private final ObjectMapper mapper;

    @Override
    public Mono<LoanApplication> saveApplication(LoanApplication loanApplication) {
        return buildLoanApplicationData(loanApplication)
                .flatMap(dataRepository::save)
                .map(data -> data.toEntityModel(loanApplication))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(SAVE_ERROR_DB));
    }

    public Mono<Void> updateOfferAndEmailStatus(Integer applicationId, String offerStatus, String emailStatus) {
        return dataRepository.findById(applicationId)
                .map(applicationData -> applicationData.updateOfferAndEmailStatus(offerStatus, emailStatus))
                .flatMap(dataRepository::save)
                .then()
                .doOnError(error -> logger.error(UPDATE_OFFER_STATUS_ERROR.getMessage(), error))
                .onErrorMap(throwTechnicalError(UPDATE_OFFER_STATUS_ERROR));
    }

    @Override
    public Flux<LoanApplication> listByProcess(String processId) {
        return dataRepository.findByProcessId(processId)
                .flatMap(data -> Mono.zip(jsonToMap(data.getRequest()), jsonToMap(data.getResponse()))
                        .map(TupleUtils.function(data::toEntityModel)))
                .onErrorMap(error -> !(error instanceof TechnicalException), throwTechnicalError(QUERY_ERROR_DB));
    }

    private Mono<LoanApplicationData> buildLoanApplicationData(LoanApplication loanApplication) {
        return Mono.zip(valueToJson(loanApplication.getRequest()), valueToJson(loanApplication.getResponse()))
                .map(tuple -> LoanApplicationData.builder()
                        .processId(loanApplication.getProcessId())
                        .otp(loanApplication.getOtp())
                        .applicationStatus(loanApplication.getApplicationStatus())
                        .request(tuple.getT1())
                        .response(tuple.getT2())
                        .build());
    }

    private <T> Mono<Json> valueToJson(T value) {
        return Mono.fromCallable(() -> mapper.writeValueAsString(value))
                .map(Json::of)
                .onErrorMap(throwTechnicalError(CONVERT_JSON_ERROR));
    }

    private Mono<Map<String, Object>> jsonToMap(Json json) {
        return Mono.fromCallable(() -> mapper.readValue(json.asString(), new TypeReference<Map<String, Object>>() {}))
                .onErrorMap(throwTechnicalError(CONVERT_MAP_ERROR));
    }

}
