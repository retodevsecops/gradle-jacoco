package com.consubanco.model.entities.loan.gateway;

import com.consubanco.model.entities.loan.vo.ApplicationResponseVO;
import com.consubanco.model.entities.process.Process;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface LoanGateway {
    Mono<Map<String, Object>> buildApplicationData(String createApplicationTemplate, Map<String, Object> data);

    Mono<ApplicationResponseVO> createApplication(Map<String, Object> applicationData);
    Mono<String> sendMail(Process process, String signedRecordAsBase64);
}