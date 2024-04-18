package com.consubanco.consumer.adapters.process;

import com.consubanco.consumer.adapters.document.OfferApiConsumer;
import com.consubanco.model.entities.process.Process;
import com.consubanco.model.entities.process.gateway.ProcessGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProcessAdapter implements ProcessGateway {

    private final OfferApiConsumer offerApiConsumer;

    @Override
    @Cacheable("process")
    public Mono<Process> getProcessById(String id) {
        return offerApiConsumer.activeOfferByProcess(id)
                .map(processMap -> {
                    String agreementId = getAgreementId(processMap);
                    String offerId = getOfferId(processMap);
                    List<String> loansId = getLoansFromOffer(processMap);
                    return new Process(id, agreementId, offerId, loansId);
                });
    }

    private String getOfferId(Map<String, Object> process) {
        return Optional.ofNullable(process)
                .map(map -> (Map<String, Object>) map.get("offer"))
                .map(offerMap -> (String) offerMap.get("id"))
                .orElse(null);
    }

    private List<String> getLoansFromOffer(Map<String, Object> process) {
        return Optional.ofNullable(process)
                .map(map -> (Map<String, Object>) map.get("offer"))
                .map(offerMap -> (List<Map<String, Object>>) offerMap.get("creditList"))
                .orElse(Collections.emptyList())  // Devuelve una lista vacía si process es nulo o no contiene la clave "offer"
                .stream()
                .map(creditMap -> (String) creditMap.get("number"))
                .toList();
    }

    private String getAgreementId(Map<String, Object> process) {
        return Optional.ofNullable(process)
                .map(map -> (Map<String, Object>) map.get("offer"))
                .map(offerMap -> (Map<String, Object>) offerMap.get("agreement"))
                .map(agreementMap -> (String) agreementMap.get("key"))
                .orElse(null);
    }

}
