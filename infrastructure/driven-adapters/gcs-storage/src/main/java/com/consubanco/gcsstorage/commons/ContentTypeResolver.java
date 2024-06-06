package com.consubanco.gcsstorage.commons;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ContentTypeResolver {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final Map<String, String> contentTypeMap = new HashMap<>();

    static {
        contentTypeMap.put("pdf", "application/pdf");
        contentTypeMap.put("jpg", "image/jpeg");
        contentTypeMap.put("jpeg", "image/jpeg");
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("json", "application/json");
        contentTypeMap.put("ftl", "text/plain");
    }

    public static String getFromFileExtension(String fileExtension) {
        return contentTypeMap.getOrDefault(fileExtension.toLowerCase(), DEFAULT_CONTENT_TYPE);
    }

}
