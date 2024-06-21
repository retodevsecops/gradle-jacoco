package com.consubanco.model.entities.loan.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoanBusinessMessage implements IExceptionMessage {

    PROCESS_NOT_FOUND("BE_LOAN_0001", "No process found with this id."),
    APPLICANT_RECORD_NOT_FOUND("BE_LOAN_0002", "The applicant record to generate the nom151 was not found."),
    FILES_NOT_FOUND("BE_LOAN_0003", "No offer files found"),
    API_CREATE_APPLICATION_RESPONSE_ERROR("BE_LOAN_0004", "Create application failed because the api responded with error."),
    MISSING_DOCUMENTS("BE_LOAN_0005", "Documents are missing to process the credit application."),
    CUSTOMER_ATTRIBUTE_NOT_FOUND("BE_LOAN_0006", "Customer Attribute not found."),
    APPLICANT_RECORD_SIGNED_NOT_FOUND("BE_LOAN_0007","The applicant record to send contract Email was not found");

    private final String code;
    private final String message;

}
