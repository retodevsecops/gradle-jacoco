package com.consubanco.freemarker.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Component
public class FunctionsUtil {

    private static final String LAST_PAY_STUB = "recibo-nomina-0";
    private static final String DOCUMENT_NAME = "document";
    private static final String DOCUMENT_DATA = "data";
    private static final String FOLIO_FISCAL = "folio-fiscal";
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String EMPTY = "";
    private static final String REPLACE = "-";
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

    @SuppressWarnings("unchecked")
    public static String getFolioFiscal(List<Map<String, Object>> ocrDocuments) {
        return ocrDocuments.stream()
                .filter(ocrDocument -> LAST_PAY_STUB.equals(ocrDocument.get(DOCUMENT_NAME)))
                .findFirst()
                .map(ocrDocument -> (List<Map<String, Object>>) ocrDocument.get(DOCUMENT_DATA))
                .flatMap(data -> data.stream()
                        .filter(item -> FOLIO_FISCAL.equals(item.get(NAME)))
                        .map(item -> (String) item.get(VALUE))
                        .findFirst())
                .map(fiscalFolio -> fiscalFolio.replace(REPLACE, EMPTY))
                .orElse(EMPTY);
    }

}