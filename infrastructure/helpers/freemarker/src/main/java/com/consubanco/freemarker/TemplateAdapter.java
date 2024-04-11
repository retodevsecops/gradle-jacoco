package com.consubanco.freemarker;

import com.consubanco.freemarker.util.FunctionsUtil;
import com.consubanco.logger.CustomLogger;
import freemarker.template.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.consubanco.freemarker.Constants.*;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class TemplateAdapter implements ITemplateOperations {

    private final CustomLogger logger;
    private final Configuration configuration;
    private final TemplateHashModel templateHashModel;

    @Override
    public <T> Mono<T> process(String templateAsString, Object data, Class<T> cls) {
        return Mono.fromCallable(() -> {
            try (StringWriter writer = new StringWriter()) {
                Template template = buildTemplate(templateAsString);
                Map<String, Object> dataMap = buildDataMap(data);
                template.process(dataMap, writer);
                String rendered = writer.toString();
                return FunctionsUtil.readLValue(rendered, cls);
            } catch (IOException | TemplateException exception) {
                logger.error(MESSAGE_ERROR, mapLogError(data, templateAsString, cls, exception));
                throw new Exception(exception);
            }
        });
    }

    @Override
    public boolean validate(String templateAsString) {
        return templateAsString.contains("${") && templateAsString.contains("<#assign");
    }

    private Template buildTemplate(String templateAsString) throws IOException {
        StringReader stringReader = new StringReader(templateAsString);
        return new Template(TEMPLATE_KEY, stringReader, this.configuration);
    }

    private Map<String, Object> buildDataMap(Object data) throws TemplateModelException {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put(FUNCTIONS_KEY, templateHashModel.get(FunctionsUtil.class.getName()));
        ofNullable(data).ifPresent(val -> map.putAll(FunctionsUtil.convertValue(val, Map.class)));
        return map;
    }

    private <T> Map<String, Object> mapLogError(Object data, String template, Class<T> cls, Throwable exception) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put(DATA_KEY, data);
        map.put(TEMPLATE_KEY, template);
        map.put(CLASS_KEY, cls.getName());
        map.put(EXCEPTION_KEY, exception.getMessage());
        ofNullable(exception.getCause()).ifPresent(cause -> map.put(CAUSE_KEY, cause.getMessage()));
        return map;
    }

}