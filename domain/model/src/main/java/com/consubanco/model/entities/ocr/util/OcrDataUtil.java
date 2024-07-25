package com.consubanco.model.entities.ocr.util;

import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@UtilityClass
public class OcrDataUtil {

    public static Optional<OcrDataVO> getByName(List<OcrDataVO> ocrData, String name) {
        return ocrData.stream()
                .filter(data -> data.getName().equalsIgnoreCase(name))
                .findFirst();
    }

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
