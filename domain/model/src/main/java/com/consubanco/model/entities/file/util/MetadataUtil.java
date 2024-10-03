package com.consubanco.model.entities.file.util;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class MetadataUtil {

    private static final String RETRIEVED_METADATA = "retrieved_from_previous_application";
    private static final String TRUE_VALUE = "true";

    public Map<String, String> createMetadataForRetrievedFile() {
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put(RETRIEVED_METADATA, "true");
        return metadata;
    }

    public static boolean isRetrievedFile(Map<String, String> metadata) {
        return metadata != null && Objects.equals(metadata.get(RETRIEVED_METADATA), TRUE_VALUE);
    }

}
