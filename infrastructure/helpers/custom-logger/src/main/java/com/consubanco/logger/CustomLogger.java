package com.consubanco.logger;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CustomLogger {

    private final String appName;
    private final Logger logger;

    public CustomLogger(@Value("${spring.application.name}") String appName) {
        this.appName = appName;
        this.logger = LogManager.getLogger(appName);
    }

    public <T> void error(T data) {
        logger.error(new ObjectMessage(data));
    }

    public <T> void info(T data) {
        logger.info(new ObjectMessage(data));
    }

}
