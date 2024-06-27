package com.consubanco.usecase.document;

import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.agreement.GetAgreementConfigUseCase;
import com.consubanco.usecase.ocr.helpers.GetOcrAttachmentsHelper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BuildPayloadDocumentUseCase {

    private static final String OCR_DOCUMENTS_KEY = "ocr_documents_data";
    private final FileRepository fileRepository;
    private final PayloadDocumentGateway payloadGateway;
    private final GetAgreementConfigUseCase agreementConfigUseCase;
    private final GetOcrAttachmentsHelper getOcrAttachmentsHelper;

    public Mono<Map<String, Object>> execute(Process process) {
        return Mono.zip(agreementConfigUseCase.execute(process.getAgreementNumber()), getPayloadTemplate())
                .flatMap(tuple -> buildPayload(process, tuple.getT1(), tuple.getT2()));
    }

    private Mono<String> getPayloadTemplate() {
        return fileRepository.getPayloadTemplateWithoutSignedUrl()
                .map(File::getContent);
    }

    private Mono<Map<String, Object>> buildPayload(Process process, AgreementConfigVO agreementConfig, String template) {
        Mono<Map<String, Object>> allData = payloadGateway.getAllData(process.getId(), agreementConfig);
        Mono<List<OcrDocument>> ocrDocuments = getOcrAttachmentsHelper.execute(process, agreementConfig);
        return Mono.zip(allData, ocrDocuments)
                .map(TupleUtils.function(this::joinData))
                .flatMap(data -> payloadGateway.buildPayload(template, data));
    }

    private Map<String, Object> joinData(Map<String, Object> data, List<OcrDocument> ocrDocuments) {
        data.put(OCR_DOCUMENTS_KEY, ocrDocuments);
        return data;
    }

}
