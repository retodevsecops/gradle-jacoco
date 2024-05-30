package com.consubanco.model.entities.loan.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@RequiredArgsConstructor
public class ApplicationResponseVO {
    private final String applicationStatus;
    private final Map<String, Object> applicationResponse;
}
