package com.consubanco.initializer;

import com.consubanco.logger.CustomLogger;
import com.consubanco.usecase.agreement.AgreementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AgreementInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${app.init.agreements}")
    private final List<String> agreements;
    private final AgreementUseCase agreementUseCase;
    private final CustomLogger logger;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        loadAgreementsInCache();
    }

    public void loadAgreementsInCache() {
        Mono.just(agreements)
                .doOnNext(list -> logger.info("Starting loading of cached agreements..." + list.toString()))
                .flatMapMany(Flux::fromIterable)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(agreementUseCase::findByNumber)
                .doOnError(logger::error)
                .sequential()
                .doFinally(e -> logger.info("Loading of cached agreements completed successfully."))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

}
