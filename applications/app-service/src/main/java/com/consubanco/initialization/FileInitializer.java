package com.consubanco.initialization;

import com.consubanco.logger.CustomLogger;
import com.consubanco.model.entities.file.File;
import com.consubanco.usecase.file.FileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

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
        fileUseCase.getPayloadTemplate()
                .doOnNext(this::printLogInfo)
                .doOnError(this::printLogError)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    private void printLogInfo(File file) {
        logger.info(Map.of(
                "message", "Cache file successfully loaded.",
                "file", file)
        );
    }

    private void printLogError(Throwable exception) {
        logger.error(Map.of(
                "message", "An error occurred while loading the payload-template file in cache.",
                "error", exception)
        );
    }

}
