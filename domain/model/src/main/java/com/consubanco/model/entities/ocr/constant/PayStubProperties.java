package com.consubanco.model.entities.ocr.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayStubProperties {

    FISCAL_FOLIO("folio-fiscal"),
    INITIAL_PERIOD_PAYMENT("periodo-inicial-pago"),
    FINAL_PERIOD_PAYMENT("periodo-final-pago");


    private final String key;
}