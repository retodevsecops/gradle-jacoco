package com.consubanco.logger;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnvironmentUtil {

    private final Environment environment;

    public String getProperty(String sourceMessage) {
        return environment.getProperty(sourceMessage);
    }

}
