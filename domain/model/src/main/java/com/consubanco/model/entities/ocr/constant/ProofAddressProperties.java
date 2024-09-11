package com.consubanco.model.entities.ocr.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProofAddressProperties {

    ZIP_CDE("codigo-postal");

    private final String key;
}