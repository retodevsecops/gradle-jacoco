package com.consubanco.consumer.adapters.agreement;

import com.consubanco.consumer.services.promoter.PromoterApiService;
import com.consubanco.model.entities.agreement.gateway.PromoterGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PromoterConsumerAdapter implements PromoterGateway {

    private final PromoterApiService promoterApiService;

    @Override
    public Mono<Map<String, Object>> findById(String promoterId) {
        return promoterApiService.getPromoterById(promoterId);
    }

}
