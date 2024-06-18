package com.consubanco.model.entities.ocr.constant;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.consubanco.model.entities.ocr.message.OcrBusinessMessage.UNDEFINED_TYPE;
import static com.consubanco.model.entities.ocr.message.OcrMessage.typeNotFound;

@Getter
@RequiredArgsConstructor
public enum OcrDocumentType {

    INE("INE", "identificacion-oficial"),
    PAY_STUBS("RecibosNomina", "recibo-nomina"),
    PROOF_ADDRESS("ComprobanteDomicilio", "comprobante-domicilio");

    private final String type;
    private final String relatedDocument;

    public static OcrDocumentType getTypeFromName(String fileName) {
        return Arrays.stream(OcrDocumentType.values())
                .filter(docType -> fileName.contains(docType.getRelatedDocument()))
                .findFirst()
                .orElseThrow(() -> ExceptionFactory.buildBusiness(typeNotFound(fileName), UNDEFINED_TYPE));
    }

}
