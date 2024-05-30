package com.consubanco.initializer;

import com.consubanco.logger.CustomLogger;
import com.consubanco.model.entities.file.File;
import com.consubanco.usecase.file.FileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

@Component
@RequiredArgsConstructor
public class FileInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final CustomLogger logger;
    private final FileUseCase fileUseCase;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        loadPayloadTemplateInCache();
    }

    private void loadPayloadTemplateInCache() {
        Mono.zip(fileUseCase.loadPayloadTemplate(), fileUseCase.loadCreateApplicationTemplate())
                .doOnNext(this::printLogInfo)
                .doOnError(this::printLogError)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    private void printLogInfo(Tuple2<File, File> tuple) {
        String firstTemplate = tuple.getT1().getName();
        String secondTemplate = tuple.getT2().getName();
        String message = String.format("The files %s, %s successfully loaded in cache.", firstTemplate, secondTemplate);
        logger.info(message);
    }

    private void printLogError(Throwable exception) {
        logger.error("An error occurred while loading the templates files in cache.", exception);
    }

}
