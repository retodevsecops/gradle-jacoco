package com.consubanco.consumer.adapters.agreement;

import com.consubanco.consumer.adapters.agreement.dto.GetAgreementDetailRequestDTO;
import com.consubanco.consumer.adapters.agreement.dto.GetAgreementDetailResponseDTO;
import com.consubanco.model.entities.agreement.Agreement;
import com.consubanco.model.entities.agreement.gateways.AgreementRepository;
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
                .headers(headers -> headers.setBearerAuth("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJub21icmUiOiJQcnVlYmFzIiwiZGVzY3JpcGNpb24iOiJVc3VhcmlvIHBhcmEgaGFjZXIgcHJ1ZWJhcyBkZSBzZXJ2aWNpb3MgZGVzYXJyb2xsYWRvcyIsImlhdCI6MTY0NTg1OTYzM30.3AHLJaCOi12UY9tsvFUMhvrnHI-jZgPgOuBwAj4C7EA"))
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
