package com.consubanco.model.entities.loan.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoanBusinessMessage implements IExceptionMessage {

    PROCESS_NOT_FOUND("BE_LOAN_0001", "No process found with this id.");

    private final String code;
    private final String message;

}
