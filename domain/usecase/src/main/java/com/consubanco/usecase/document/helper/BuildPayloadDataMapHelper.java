package com.consubanco.usecase.document.helper;

import com.consubanco.model.entities.agreement.vo.AgreementConfigVO;
import com.consubanco.model.entities.document.gateway.PayloadDocumentGateway;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.process.Process;
import com.consubanco.usecase.ocr.helpers.GetOcrAttachmentsHelper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BuildPayloadDataMapHelper {

    private static final String OCR_DOCUMENTS_KEY = "ocr_documents_data";
    private final PayloadDocumentGateway payloadGateway;
    private final GetOcrAttachmentsHelper getOcrAttachmentsHelper;

    public Mono<Map<String, Object>> execute(Process process, AgreementConfigVO agreementConfig) {
        Mono<Map<String, Object>> allData = payloadGateway.getAllData(process.getId(), agreementConfig);
        Mono<List<OcrDocument>> ocrDocuments = getOcrAttachmentsHelper.execute(process, agreementConfig);
        return Mono.zip(allData, ocrDocuments)
                .map(TupleUtils.function(this::joinData));
    }

    private Map<String, Object> joinData(Map<String, Object> data, List<OcrDocument> ocrDocuments) {
        data.put(OCR_DOCUMENTS_KEY, ocrDocumentsToMapList(ocrDocuments));
        return data;
    }

    private List<Map<String, Object>> ocrDocumentsToMapList(List<OcrDocument> ocrDocuments) {
        return ocrDocuments.parallelStream()
                .map(ocrDocument -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("document", ocrDocument.getName());
                    data.put("analysis_id", ocrDocument.getAnalysisId());
                    data.put("data", ocrDocument.getData());
                    return data;
                })
                .toList();
    }

}