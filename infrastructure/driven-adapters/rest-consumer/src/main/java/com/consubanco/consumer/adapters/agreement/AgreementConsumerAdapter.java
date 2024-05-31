package com.consubanco.consumer.adapters.agreement;

import com.consubanco.consumer.adapters.agreement.dto.GetAgreementRequestDTO;
import com.consubanco.consumer.adapters.agreement.dto.GetAgreementResponseDTO;
import com.consubanco.consumer.adapters.agreement.properties.AgreementApisProperties;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateway.AgreementGateway;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildTechnical;
import static com.consubanco.model.commons.exception.factory.ExceptionFactory.throwTechnicalError;
import static com.consubanco.model.entities.agreement.message.AgreementTechnicalMessage.API_ERROR;

@Service
public class AgreementConsumerAdapter implements AgreementGateway {

    private final WebClient clientHttp;
    private final ModelMapper modelMapper;
    private final AgreementApisProperties apis;

    public AgreementConsumerAdapter(final @Qualifier("ApiPromoterClient") WebClient clientHttp,
                                    final ModelMapper modelMapper,
                                    final AgreementApisProperties apis) {
        this.clientHttp = clientHttp;
        this.modelMapper = modelMapper;
        this.apis = apis;
    }

    @Override
    @Cacheable("agreements")
    public Mono<Agreement> findByNumber(String agreementNumber) {
        return this.clientHttp.post()
                .uri(apis.getApiGetAgreement())
                .bodyValue(new GetAgreementRequestDTO(agreementNumber, apis.getChannel()))
                .retrieve()
                .bodyToMono(GetAgreementResponseDTO.class)
                .map(response -> modelMapper.map(response.getDetail().getAgreement(), Agreement.class))
                .onErrorMap(WebClientResponseException.class, error -> buildTechnical(error.getResponseBodyAsString(), API_ERROR))
                .onErrorMap(throwTechnicalError(API_ERROR));
    }

}
