package com.consubanco.model.entities.document.gateway;

import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface PayloadDocumentGateway {
    Mono<Map<String, Object>> getAllData(String processId, AgreementConfigVO agreementConfigVO);
    Mono<Map<String, Object>> buildPayload(String template, Map<String, Object> data);
}
