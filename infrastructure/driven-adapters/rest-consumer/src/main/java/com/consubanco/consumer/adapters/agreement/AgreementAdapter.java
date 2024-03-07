package com.consubanco.consumer.adapters.agreement;

import com.consubanco.consumer.adapters.agreement.dto.GetAgreementDetailRequestDTO;
import com.consubanco.consumer.adapters.agreement.dto.GetAgreementDetailResponseDTO;
import com.consubanco.model.agreement.Agreement;
import com.consubanco.model.agreement.gateways.AgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AgreementAdapter implements AgreementRepository {

    private final WebClient clientHttp;

    @Override
    public Mono<Agreement> findByNumber(String agreementNumber) {
        return this.clientHttp.post()
                .uri("https://it-api-gateway.qa.masnominadigital.com/convenios/getDetail")
                .bodyValue(this.buildRequest(agreementNumber))
                .retrieve()
                .bodyToMono(GetAgreementDetailResponseDTO.class)
                .map(GetAgreementDetailResponseDTO::toDomainEntity);
    }

    private GetAgreementDetailRequestDTO buildRequest(String agreementNumber) {
        return GetAgreementDetailRequestDTO.builder()
                .agreementNumber(agreementNumber)
                .channel("ECSB")
                .build();
    }

}
