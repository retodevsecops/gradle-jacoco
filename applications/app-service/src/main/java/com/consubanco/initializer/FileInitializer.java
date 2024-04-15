package com.consubanco.initializer;

import com.consubanco.logger.CustomLogger;
import com.consubanco.model.entities.file.File;
import com.consubanco.usecase.file.FileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

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
        fileUseCase.loadPayloadTemplate()
                .doOnNext(this::printLogInfo)
                .doOnError(this::printLogError)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    private void printLogInfo(File file) {
        logger.info("Cache file payload-template successfully loaded.");
    }

    private void printLogError(Throwable exception) {
        logger.error("An error occurred while loading the payload-template file in cache.", exception);
    }

}
