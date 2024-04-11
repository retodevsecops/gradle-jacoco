package com.consubanco.consumer.adapters.document;

import com.consubanco.freemarker.ITemplateOperations;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PayloadDocumentAdapter implements PayloadDocumentGateway, ApplicationListener<ApplicationReadyEvent> {

    private final String promoterId;
    private final CustomLogger logger;
    private final PromoterService promoterService;
    private final ITemplateOperations templateOperations;

    public PayloadDocumentAdapter(final @Value("${app.init.promoter-id}") String promoterId,
                                  final CustomLogger logger,
                                  final PromoterService promoterService,
                                  final ITemplateOperations templateOperations) {
        this.promoterId = promoterId;
        this.logger = logger;
        this.promoterService = promoterService;
        this.templateOperations = templateOperations;
    }

    @Override
    public Mono<Map<String, Object>> getAllData() {
        return promoterService.getPromoterById(promoterId)
                .map(promoter -> {
                    Map<String, Object> data = new ConcurrentHashMap<>();
                    data.put("promoter", promoter);
                    return data;
                })
                .doOnNext(data -> logger.info("Data used to build payload was consulted.", data));
    }

    @Override
    public Mono<Map<String, Object>> buildPayload(String template, Map<String, Object> data) {
        return templateOperations.process(template, data, Map.class)
                .map(map -> (Map<String, Object>) map)
                .doOnNext(payload -> logger.info("Built payload.", payload));
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        promoterService.getPromoterById(promoterId)
                .doOnNext(data -> logger.info("Promoter data has been cached."))
                .doOnError(error -> logger.error("Failed to load cached promoter.", error))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

}
