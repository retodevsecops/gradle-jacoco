package com.consubanco.consumer.initializer;

import com.consubanco.consumer.services.CustomerApiService;
import com.consubanco.logger.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class CustomerHealthCheck implements ApplicationListener<ApplicationReadyEvent> {

    private final CustomLogger logger;
    private final CustomerApiService customerApiService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        healthTest();
    }

    private void healthTest() {
        this.customerApiService.getCustomerHealth()
                .doOnNext(rta -> logger.info("Connection with renex customer service was verified."))
                .doOnError(error -> logger.error("Failed to verify the connection to renex customer.", error.getCause()))
                .subscribeOn(Schedulers.single())
                .subscribe();
    }

}
