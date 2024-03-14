package com.consubanco.consumer.adapters.agreement;

import com.consubanco.consumer.adapters.agreement.dto.GetAgreementDetailRequestDTO;
import com.consubanco.consumer.adapters.agreement.dto.GetAgreementDetailResponseDTO;
import com.consubanco.consumer.adapters.agreement.properties.AgreementGetDetailApiProperties;
import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateways.AgreementRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.agreement.message.AgreementTechnicalMessage.API_ERROR;

@Service
public class AgreementConsumerAdapter implements AgreementRepository {

    private final WebClient clientHttp;
    private final ModelMapper modelMapper;
    private final AgreementGetDetailApiProperties agreementGetDetailApiProperties;

    public AgreementConsumerAdapter(final @Qualifier("ApiPromoterClient") WebClient clientHttp,
                                    final ModelMapper modelMapper,
                                    final AgreementGetDetailApiProperties agreementGetDetailApiProperties) {
        this.clientHttp = clientHttp;
        this.modelMapper = modelMapper;
        this.agreementGetDetailApiProperties = agreementGetDetailApiProperties;
    }

    @Override
    @Cacheable("agreements")
    public Mono<Agreement> findByNumber(String agreementNumber) {
        return this.clientHttp.post()
                .uri(agreementGetDetailApiProperties.getEndpoint())
                .bodyValue(this.buildRequest(agreementNumber))
                .retrieve()
                .bodyToMono(GetAgreementDetailResponseDTO.class)
                .map(response -> modelMapper.map(response.getDetail().getAgreement(), Agreement.class))
                .onErrorMap(error -> ExceptionFactory.buildTechnical(error, API_ERROR));
    }

    private GetAgreementDetailRequestDTO buildRequest(String agreementNumber) {
        return GetAgreementDetailRequestDTO.builder()
                .agreementNumber(agreementNumber)
                .channel(agreementGetDetailApiProperties.getChannel())
                .build();
    }

}
