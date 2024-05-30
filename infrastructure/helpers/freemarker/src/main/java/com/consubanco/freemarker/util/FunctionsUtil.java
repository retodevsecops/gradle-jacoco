package com.consubanco.freemarker.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


@Component
public class FunctionsUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Object getValue(Map<String, Object> data, String value) {
        return data.get(value);
    }

    public static <T> T convertValue(Object object, Class<T> cls) {
        return OBJECT_MAPPER.convertValue(object, cls);
    }

    public static <T> T readLValue(String message, Class<T> cls) throws IOException {
        return OBJECT_MAPPER.readValue(message, cls);

    }

}