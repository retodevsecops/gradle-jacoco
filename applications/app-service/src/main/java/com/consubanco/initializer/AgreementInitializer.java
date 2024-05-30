package com.consubanco.initializer;

import com.consubanco.logger.CustomLogger;
import com.consubanco.usecase.agreement.LoadAgreementsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class AgreementInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final LoadAgreementsUseCase loadAgreementsUseCase;
    private final CustomLogger logger;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        loadAgreementsInCache();
    }

    public void loadAgreementsInCache() {
        loadAgreementsUseCase.execute()
                .doOnNext(agreement -> logger.info("Agreement "+agreement.getNumber()+" has loaded into cache."))
                .doOnError(error -> logger.error("Error when caching agreements.", error))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

}
