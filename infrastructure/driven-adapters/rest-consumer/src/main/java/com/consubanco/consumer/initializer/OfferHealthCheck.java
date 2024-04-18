package com.consubanco.consumer.initializer;

import com.consubanco.consumer.adapters.document.OfferApiConsumer;
import com.consubanco.logger.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class OfferHealthCheck implements ApplicationListener<ApplicationReadyEvent> {

    private final CustomLogger logger;
    private final OfferApiConsumer offerApiConsumer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        healthTest();
    }

    private void healthTest() {
        this.offerApiConsumer.getOfferHealth()
                .doOnNext(rta -> logger.info("Connection with renex offer service was verified."))
                .doOnError(error -> logger.error("Failed to verify the connection to renex offer.", error.getCause()))
                .subscribeOn(Schedulers.single())
                .subscribe();
    }

}
