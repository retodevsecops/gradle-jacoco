package com.consubanco.model.entities.process.message;

import com.consubanco.model.commons.exception.message.IExceptionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProcessBusinessMessage implements IExceptionMessage {

    PROCESS_NOT_FOUND("BE_PROCESS_0001", "No process found with this id."),
    ID_PROCESS_REQUIRED("BE_PROCESS_0002", "The process identifier is required."),
    INCOMPLETE_DATA("BE_PROCESS_0003", "The process data is incomplete."),
    NOT_LOANS("BE_PROCESS_0004","The offer associated with the process has no loans.");

    private final String code;
    private final String message;

}
