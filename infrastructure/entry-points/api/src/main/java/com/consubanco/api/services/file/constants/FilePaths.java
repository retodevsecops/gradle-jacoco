package com.consubanco.api.services.file.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FilePaths {
    public static final String GENERATE_DOCUMENT_PATH = "/document/generate-url/{processId}";
    public static final String GENERATE_DOCUMENT_ENCODED_PATH = "/document/generate-encoded/{processId}";
    public static final String GET_AND_UPLOAD_DOCUMENT_PATH = "/document/generate-and-upload/{processId}";
    public static final String CNCA_PATH = "/offer/cnca/{processId}";
    public static final String DOCUMENTS_AGREEMENT_PATH = "/offer/process/{processId}";
    public static final String FILES_OFFER_PATH = "/offer/{processId}";
    public static final String FILES_CUSTOMER_VIEW_PATH = "/offer/customer-view/{processId}";
    public static final String PAYLOAD_TEMPLATE_PATH = "/management/upload-payload-template";
    public static final String AGREEMENTS_CONFIG_PATH = "/management/upload-agreements-config";
    public static final String CREATE_APPLICATION_TEMPLATE_PATH = "/management/upload-create-application-template";
    public static final String MANAGEMENT_PATH = "/management";
    public static final String PAYLOAD_DATA_PATH = "/offer/payload-data/{processId}";
    public static final String UPLOAD_OFFICIAL_ID = "/offer/upload-official-id/{processId}";
    public static final String ATTACHMENT_STATUS_PATH = "/offer/attachment-status/process/{processId}";
    public static final String VALIDATE_TEMPLATE = "validate-template-freemarker";
}
