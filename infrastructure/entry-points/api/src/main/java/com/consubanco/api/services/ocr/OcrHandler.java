package com.consubanco.api.services.ocr;

import com.consubanco.api.commons.util.FilePartUtil;
import com.consubanco.api.commons.util.HttpResponseUtil;
import com.consubanco.api.commons.util.ParamsValidator;
import com.consubanco.api.services.ocr.constants.OcrParams;
import com.consubanco.api.services.ocr.dto.OcrDocumentResDTO;
import com.consubanco.api.services.ocr.dto.OcrResulSetResDTO;
import com.consubanco.usecase.ocr.usecase.OcrDocumentUseCase;
import com.consubanco.usecase.ocr.usecase.ValidateOcrDocumentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OcrHandler {

    private final OcrDocumentUseCase ocrDocumentUseCase;
    private final ValidateOcrDocumentUseCase validateOcrDocumentUseCase;

    public Mono<ServerResponse> validateDocument(ServerRequest request) {
        String processId = request.pathVariable(OcrParams.PROCESS_ID);
        boolean applyOcr = this.getApplyOcr(request);
        return request.body(BodyExtractors.toParts())
                .ofType(FilePart.class)
                .next()
                .flatMap(FilePartUtil::buildFileUploadVOFromFilePart)
                .flatMap(fileUploadVO -> validateOcrDocumentUseCase.execute(processId, fileUploadVO, applyOcr))
                .map(OcrResulSetResDTO::new)
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> findByProcessId(ServerRequest request) {
        String processId = request.pathVariable(OcrParams.PROCESS_ID);
        return ocrDocumentUseCase.findByProcessId(processId)
                .map(OcrDocumentResDTO::new)
                .collectList()
                .flatMap(HttpResponseUtil::ok);
    }


    public Mono<ServerResponse> findByAnalysisId(ServerRequest request) {
        return ParamsValidator.queryParam(request, OcrParams.ANALYSIS_ID)
                .flatMap(ocrDocumentUseCase::findByAnalysisId)
                .map(OcrDocumentResDTO::new)
                .flatMap(HttpResponseUtil::ok);
    }

    public Mono<ServerResponse> findByDocumentName(ServerRequest request) {
        String processId = request.pathVariable(OcrParams.PROCESS_ID);
        String documentName = request.pathVariable(OcrParams.DOCUMENT_NAME);
        return ocrDocumentUseCase.findByDocumentName(processId, documentName)
                .map(OcrResulSetResDTO::new)
                .flatMap(HttpResponseUtil::ok);
    }

    private boolean getApplyOcr(ServerRequest request) {
        return request.queryParam(OcrParams.APPLY_OCR)
                .map(Boolean::parseBoolean)
                .orElse(true);
    }


}
