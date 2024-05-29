package com.consubanco.model.entities.loan.gateway;

import com.consubanco.model.entities.loan.vo.ApplicationResponseVO;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface LoanGateway {
    Mono<Map<String, Object>> buildApplicationData(String createApplicationTemplate, Map<String, Object> data);
    Mono<ApplicationResponseVO> createApplication(Map<String, Object> applicationData);
}
