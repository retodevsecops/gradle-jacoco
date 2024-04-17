package com.consubanco.model.entities.agreement.gateway;

import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import reactor.core.publisher.Flux;

public interface AgreementConfigRepository {
    Flux<AgreementConfigVO> getAllConfig();
}
