package com.consubanco.model.entities.document.gateway;

import com.consubanco.model.entities.document.vo.DocumentSignatureRequestVO;
import reactor.core.publisher.Mono;

public interface SignedDocumentGateway {

    Mono<Boolean> loadDocumentForCSB(DocumentSignatureRequestVO signatureRequest);
    Mono<Boolean> loadDocumentForMN(DocumentSignatureRequestVO signatureRequest);

    Mono<String> getSignedDocumentForCSB(String documentId);

    Mono<String> getSignedDocumentForMN(String documentId);

    Mono<String> getNom151ForCSB(String documentId);

    Mono<String> getNom151ForMN(String documentId);
    Integer getValidDays();

}
