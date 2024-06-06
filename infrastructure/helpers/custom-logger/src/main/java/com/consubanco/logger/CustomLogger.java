package com.consubanco.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Component
public class CustomLogger {

    private static final String TITLE_KEY = "title";
    private static final String DATA_KEY = "data";
    private static final String FORMAT_STRING_DATA = "\"%s\"";

    private final String appName;
    private final Logger logger;
    private final ObjectMapper mapper;

    public CustomLogger(@Value("${spring.application.name}") String appName,
                        final ObjectMapper mapper) {
        this.appName = appName;
        this.logger = LogManager.getLogger(appName);
        this.mapper = mapper;

    }

    public <T> void error(T data) {
        this.logDataError(data);
    }

    public <T> void error(String message, T data) {
        this.logDataError(buildMap(message, data));
    }

    public <T> void info(T data) {
        this.logDataInfo(data);
    }

    public <T> void info(String message, T data) {
        this.logDataInfo(buildMap(message, data));
    }

    private <T> Map<String, Object> buildMap(String message, T data) {
        return Map.of(TITLE_KEY, message, DATA_KEY, data);
    }

    private <T> void logDataInfo(T data) {
        try {
            logger.info((data instanceof String) ? String.format(FORMAT_STRING_DATA, data) : mapper.writeValueAsString(data));
        } catch (JsonProcessingException error) {
            logger.info("\""+data+"\"");
        }
    }

    private <T> void logDataError(T data) {
        try {
            logger.error((data instanceof String) ? String.format(FORMAT_STRING_DATA, data) : mapper.writeValueAsString(data));
        } catch (JsonProcessingException error) {
            logger.error("\"{}\"", data);
        }
    }

}
