package com.consubanco.gcsstorage.adapters.agreement;

import com.consubanco.model.entities.agreement.gateway.AgreementConfigRepository;
import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgreementStorageAdapter implements AgreementConfigRepository {

    private final AgreementConfigStorageService agreementConfigStorageService;

    @Override
    public Mono<List<AgreementConfigVO>> getAgreementsConfig() {
        return agreementConfigStorageService.getAgreementsConfig();
    }

    @Override
    public Mono<AgreementConfigVO> getConfigByAgreement(String agreementNumber) {
        return agreementConfigStorageService.getAgreementsConfig()
                .flatMapMany(Flux::fromIterable)
                .filter(agreementConfigVO -> agreementConfigVO.getAgreementNumber().equalsIgnoreCase(agreementNumber))
                .next();
    }

}
