package com.consubanco.api.services.ocr;

import com.consubanco.api.commons.swagger.ParamsOpenAPI;
import com.consubanco.api.commons.swagger.RequestsOpenAPI;
import com.consubanco.api.services.ocr.constants.OcrParams;
import com.consubanco.api.services.ocr.dto.OcrDocumentResDTO;
import com.consubanco.api.services.ocr.dto.OcrResulSetResDTO;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static com.consubanco.api.commons.swagger.ResponsesOpenAPI.*;

@UtilityClass
public class OcrOpenAPI {

    private static final String TAG_OCR = "Ocr Documents";

    public static Consumer<Builder> validateDocument() {
        return ops -> ops.tag(TAG_OCR)
                .operationId("validateDocument")
                .description("Upload attachment to storage to optionally be validated by ocr process.")
                .summary("Upload attachment to storage with validate ocr process.")
                .parameter(ParamsOpenAPI.path(OcrParams.PROCESS_ID, "Process identifier"))
                .parameter(ParamsOpenAPI.query(OcrParams.APPLY_OCR, "Should apply ocr validation"))
                .requestBody(RequestsOpenAPI.formData())
                .response(responseOk(OcrResulSetResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> findByAnalysisId() {
        return ops -> ops.tag(TAG_OCR)
                .operationId("findByAnalysisId")
                .description("Find ocr document by analysis identifier.")
                .summary("Find ocr document by analysis id.")
                .parameter(ParamsOpenAPI.query(OcrParams.ANALYSIS_ID, "Analysis identifier"))
                .response(responseOk(OcrDocumentResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> findByProcessId() {
        return ops -> ops.tag(TAG_OCR)
                .operationId("findByProcessId")
                .description("List documents validated with ocr by process id.")
                .summary("List ocr documents by process id.")
                .parameter(ParamsOpenAPI.path(OcrParams.PROCESS_ID, "Process identifier"))
                .response(responseOkWithList(OcrResulSetResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

    public static Consumer<Builder> findByDocumentName() {
        return ops -> ops.tag(TAG_OCR)
                .operationId("findByDocumentName")
                .description("Find ocr document by document name of the process.")
                .summary("Find ocr document by document name of the process.")
                .parameter(ParamsOpenAPI.path(OcrParams.PROCESS_ID, "Process identifier"))
                .parameter(ParamsOpenAPI.path(OcrParams.DOCUMENT_NAME, "Document name"))
                .response(responseOk(OcrResulSetResDTO.class))
                .response(responseBusinessException())
                .response(responseInternalError());
    }

}
