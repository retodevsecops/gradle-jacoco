package com.consubanco.consumer.adapters.process;

import com.consubanco.consumer.adapters.process.dto.ActiveOfferingResDTO;
import com.consubanco.consumer.services.OfferApiService;
import com.consubanco.model.entities.process.Process;
import com.consubanco.model.entities.process.gateway.ProcessGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProcessAdapter implements ProcessGateway {

    private final OfferApiService offerApiService;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable("process")
    public Mono<Process> getProcessById(String processId) {
        return offerApiService.activeOfferByProcess(processId)
                .map(offerDataMap -> objectMapper.convertValue(offerDataMap, ActiveOfferingResDTO.class))
                .map(dto -> dto.toDomainEntity(processId));
    }

    @Override
    @CacheEvict(cacheNames = {"process", "customers", "customers-biometrics"})
    public Mono<String> finish(String processId) {
        return offerApiService.acceptOffer(processId);
    }

}
