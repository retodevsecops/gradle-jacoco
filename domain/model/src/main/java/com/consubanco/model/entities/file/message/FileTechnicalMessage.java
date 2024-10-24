package com.consubanco.model.entities.file.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileTechnicalMessage implements IExceptionMessage {

    API_ERROR("TE_FILE_0001", "Error consuming getCNCALetter capability of OPC Service API."),
    API_PROMOTER_ERROR("TE_FILE_0002", "Error when consulting the promoter API to generate document."),
    READING_ERROR("TE_FILE_0003", "Error encoding file."),
    ENCODED_ERROR("TE_FILE_0004", "An error occurred while encoding the file."),
    CNCA_LETTER_ERROR("TE_FILE_0005", "Error when generating the cnca letter."),
    STORAGE_ERROR("TE_FILE_0006", "Error when save file in to storage."),
    SIGN_URL_ERROR("TE_FILE_0007", "An error occurred while signing file url."),
    GET_FILE_ERROR("TE_FILE_0007", "An error occurred while get files in storage."),
    FIND_FILE_ERROR("TE_FILE_0008", "An error occurred while find file in storage."),
    LOCAL_TEMPLATE_ERROR("TE_FILE_0009", "Error loading local template file."),
    API_REQUEST_ERROR("TE_FILE_0010", "Error in the api consumption request."),
    DELETE_FILE_ERROR("TE_FILE_0011","Error when delete file in to storage");

    private final String code;
    private final String message;

}
