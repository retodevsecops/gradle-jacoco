package com.consubanco.consumer.adapters.agreement;

import com.consubanco.consumer.adapters.agreement.dto.GetAgreementDetailRequestDTO;
import com.consubanco.consumer.adapters.agreement.dto.GetAgreementDetailResponseDTO;
import com.consubanco.consumer.adapters.agreement.properties.AgreementGetDetailApiProperties;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateways.AgreementRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AgreementAdapter implements AgreementRepository {

    private final WebClient clientHttp;
    private final ModelMapper modelMapper;
    private final AgreementGetDetailApiProperties agreementGetDetailApiProperties;


    @Override
    public Mono<Agreement> findByNumber(String agreementNumber) {
        System.out.println(agreementGetDetailApiProperties.getEndpoint());
        return this.clientHttp.post()
                .uri(agreementGetDetailApiProperties.getEndpoint())
                .headers(headers -> headers.setBearerAuth(agreementGetDetailApiProperties.getAuthToken()))
                .bodyValue(this.buildRequest(agreementNumber))
                .retrieve()
                .bodyToMono(GetAgreementDetailResponseDTO.class)
                .map(response -> modelMapper.map(response.getDetail().getAgreement(), Agreement.class));
    }

    private GetAgreementDetailRequestDTO buildRequest(String agreementNumber) {
        return GetAgreementDetailRequestDTO.builder()
                .agreementNumber(agreementNumber)
                .channel(agreementGetDetailApiProperties.getChannel())
                .build();
    }

}
