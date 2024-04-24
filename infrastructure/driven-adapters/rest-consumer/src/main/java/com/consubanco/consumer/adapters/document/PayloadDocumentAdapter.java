package com.consubanco.consumer.adapters.document;

import com.consubanco.freemarker.ITemplateOperations;
import com.consubanco.logger.CustomLogger;
import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PayloadDocumentAdapter implements PayloadDocumentGateway, ApplicationListener<ApplicationReadyEvent> {

    private final String promoterId;
    private final CustomLogger logger;
    private final PromoterApiConsumer promoterApiConsumer;
    private final CustomerApiConsumer customerApiConsumer;
    private final OfferApiConsumer offerApiConsumer;
    private final ITemplateOperations templateOperations;

    public PayloadDocumentAdapter(final @Value("${app.init.promoter-id}") String promoterId,
                                  final CustomLogger logger,
                                  final PromoterApiConsumer promoterApiConsumer,
                                  final CustomerApiConsumer customerApiConsumer,
                                  final OfferApiConsumer offerApiConsumer,
                                  final ITemplateOperations templateOperations) {
        this.promoterId = promoterId;
        this.logger = logger;
        this.promoterApiConsumer = promoterApiConsumer;
        this.customerApiConsumer = customerApiConsumer;
        this.offerApiConsumer = offerApiConsumer;
        this.templateOperations = templateOperations;
    }

    @Override
    @Cacheable("payload-data")
    public Mono<Map<String, Object>> getAllData(String processId) {
        return Mono.zip(promoterApiConsumer.getPromoterById(promoterId),
                        customerApiConsumer.customerDataByProcess(processId),
                        offerApiConsumer.activeOfferByProcess(processId))
                .map(this::buildDataMap)
                .doOnNext(data -> logger.info("Data used to build payload was consulted.", data));
    }

    private Map<String, Object> buildDataMap(Tuple3<Map<String, Object>, Map<String, Object>, Map<String, Object>> tuple) {
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("promoter_data", tuple.getT1());
        data.put("customer_data", tuple.getT2());
        data.put("offer_data", tuple.getT3());
        data.put("agreement_data", "");
        return data;
    }

    @Override
    public Mono<Map<String, Object>> buildPayload(String template, Map<String, Object> data) {
        return templateOperations.process(template, data, Map.class)
                .map(map -> (Map<String, Object>) map)
                .doOnNext(payload -> logger.info("Built payload.", payload));
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        promoterApiConsumer.getPromoterById(promoterId)
                .doOnNext(data -> logger.info("Promoter data has been cached."))
                .doOnError(error -> logger.error("Failed to load cached promoter.", error))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

}
