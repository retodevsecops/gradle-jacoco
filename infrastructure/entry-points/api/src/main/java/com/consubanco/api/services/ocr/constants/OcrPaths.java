package com.consubanco.api.services.ocr.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OcrPaths {
    public static final String PROCESS_PATH = "/{processId}";
    public static final String FIND_BY_DOCUMENT_NAME_PATH = "/{processId}/{documentName}";
}
