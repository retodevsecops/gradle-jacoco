package com.consubanco.api.services.file.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FilePaths {
    public static final String CNCA_PATH = "/document/cnca";
    public static final String GENERATE_DOCUMENT_PATH = "/document/generate-url";
    public static final String GENERATE_DOCUMENT_ENCODED_PATH = "/document/generate-encoded";
    public static final String GET_AND_UPLOAD_DOCUMENT_PATH = "/document/generate-and-upload";
    public static final String DOCUMENTS_AGREEMENT_PATH = "/offer/{offerId}/agreement/{agreementNumber}";
    public static final String DOCUMENTS_OFFER_PATH = "/offer/{offerId}";
}
