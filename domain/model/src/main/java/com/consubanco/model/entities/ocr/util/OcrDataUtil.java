package com.consubanco.model.entities.ocr.util;

import com.consubanco.model.entities.ocr.OcrDocument;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class OcrDataUtil {

    public static List<Map<String, Object>> ocrDocumentsToMapList(List<OcrDocument> ocrDocuments) {
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
