package com.consubanco.consumer.initializer;

import com.consubanco.consumer.services.OfferApiService;
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
    private final OfferApiService offerApiService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        healthTest();
    }

    private void healthTest() {
        this.offerApiService.getOfferHealth()
                .doOnNext(rta -> logger.info("Connection with renex offer service was verified."))
                .doOnError(error -> logger.error("Failed to verify the connection to renex offer.", error.getCause()))
                .subscribeOn(Schedulers.single())
                .subscribe();
    }

}
