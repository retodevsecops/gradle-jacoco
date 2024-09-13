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

    private final static String LOG_SUCCESS = "Agreements: %s and promoter: %s has loaded into cache.";

    private final LoadAgreementsUseCase loadAgreementsUseCase;
    private final CustomLogger logger;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        loadAgreementsInCache();
    }

    public void loadAgreementsInCache() {
        loadAgreementsUseCase.execute()
                .doOnNext(tuple -> logger.info(String.format(LOG_SUCCESS, tuple.getT1(), tuple.getT2())))
                .doOnError(error -> logger.error("Error when caching agreements and promoters.", error))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

}
