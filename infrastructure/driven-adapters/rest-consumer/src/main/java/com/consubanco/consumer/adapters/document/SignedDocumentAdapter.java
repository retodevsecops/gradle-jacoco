package com.consubanco.consumer.adapters.document;

import com.consubanco.consumer.adapters.document.dto.CustomerAllInfoResDTO;
import com.consubanco.consumer.services.CustomerApiService;
import com.consubanco.consumer.services.nom151.Nom151ApiService;
import com.consubanco.consumer.services.nom151.util.LoadDocumentReqDTO;
import com.consubanco.model.entities.document.gateway.SignedDocumentGateway;
import com.consubanco.model.entities.document.vo.DocumentSignatureRequestVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SignedDocumentAdapter implements SignedDocumentGateway {

    private final Nom151ApiService nom151ApiService;
    private final CustomerApiService customerApiService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Boolean> loadDocumentForCSB(DocumentSignatureRequestVO signatureRequest) {
        return buildRequestDTO(signatureRequest)
                .flatMap(nom151ApiService::loadDocumentForCSB);
    }

    @Override
    public Mono<Boolean> loadDocumentForMN(DocumentSignatureRequestVO signatureRequest) {
        return buildRequestDTO(signatureRequest)
                .flatMap(nom151ApiService::loadDocumentForMN);
    }

    @Override
    public Mono<String> getSignedDocumentForCSB(String documentId) {
        return nom151ApiService.getSignedDocumentForCSB(documentId);
    }

    @Override
    public Mono<String> getSignedDocumentForMN(String documentId) {
        return nom151ApiService.getSignedDocumentForMN(documentId);
    }

    @Override
    public Mono<String> getNom151ForCSB(String documentId) {
        return nom151ApiService.getNom151ForCSB(documentId);
    }

    @Override
    public Mono<String> getNom151ForMN(String documentId) {
        return nom151ApiService.getNom151ForMN(documentId);
    }

    @Override
    public Integer getValidDays() {
        return nom151ApiService.getValidDays();
    }

    private Mono<LoadDocumentReqDTO> buildRequestDTO(DocumentSignatureRequestVO signatureRequest) {
        return customerApiService.customerDataByProcess(signatureRequest.getProcessId())
                .map(mapCustomerData -> objectMapper.convertValue(mapCustomerData, CustomerAllInfoResDTO.class))
                .map(customerData -> buildDTO(customerData, signatureRequest));

    }

    private LoadDocumentReqDTO buildDTO(CustomerAllInfoResDTO customer, DocumentSignatureRequestVO signatureRequest) {
        return LoadDocumentReqDTO.builder()
                .documentId(signatureRequest.getId())
                .documentInBase64(signatureRequest.getDocumentInBase64())
                .names(customer.getNames())
                .paternalLastname(customer.getCustomer().getLastName())
                .motherLastname(customer.getCustomer().getSecondLastName())
                .rfc(customer.getCustomer().getRfc())
                .email(customer.getCustomer().getEmail())
                .showSignatures(signatureRequest.getShowSignatures())
                .build();
    }

}
