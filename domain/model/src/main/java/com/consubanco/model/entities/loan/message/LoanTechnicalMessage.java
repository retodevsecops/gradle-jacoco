package com.consubanco.model.entities.loan.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoanTechnicalMessage implements IExceptionMessage {

    API_CREATE_APPLICATION_ERROR("TE_LOAN_0001", "Error when consuming the create application api."),
    SAVE_ERROR_DB("TE_LOAN_0002", "Error when saving in the database."),
    CONVERT_JSON_ERROR("TE_LOAN_0003", "Error converting object to json."),
    CONVERT_MAP_ERROR("TE_LOAN_0004", "Error converting json to map."),
    QUERY_ERROR_DB("TE_LOAN_0005", "Error when querying the data base.");

    private final String code;
    private final String message;

}
